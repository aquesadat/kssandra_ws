/**
 * 
 */
package com.kssandra.ksd_ws.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.kssandra.ksd_common.dto.CryptoCurrencyDto;
import com.kssandra.ksd_common.dto.PredictionDto;
import com.kssandra.ksd_common.dto.PredictionSuccessDto;
import com.kssandra.ksd_common.util.DateUtils;
import com.kssandra.ksd_persistence.dao.CryptoCurrencyDao;
import com.kssandra.ksd_persistence.dao.CryptoDataDao;
import com.kssandra.ksd_persistence.dao.PredictionDao;
import com.kssandra.ksd_persistence.dao.PredictionSuccessDao;
import com.kssandra.ksd_ws.enums.IntervalEnum;
import com.kssandra.ksd_ws.exception.KsdServiceException;
import com.kssandra.ksd_ws.request.IntradaySimulationRequest;
import com.kssandra.ksd_ws.response.IntradaySimulationResponse;
import com.kssandra.ksd_ws.response.IntradaySimulationResponseItem;

/**
 * Test class for IntradaySimulationService
 * 
 * @author aquesada
 *
 */
@SpringBootTest
class IntradaySimulationServiceTest {

	@MockBean
	CryptoCurrencyDao cxCurrDao;

	@MockBean
	CryptoDataDao cxDataDao;

	@MockBean
	PredictionDao predictionDao;

	@Autowired
	IntradaySimulationService intradaySimulationService;

	@MockBean
	PredictionSuccessDao predictionSuccessDao;

	@Captor
	ArgumentCaptor<LocalDateTime> captorDateFrom, captorDateTo;

	/**
	 * Test method for getSimulation
	 * {@link com.kssandra.ksd_ws.service.IntradaySimulationService#getSimulation(com.kssandra.ksd_ws.request.IntradaySimulationRequest)}.
	 * 
	 * @throws KsdServiceException
	 */
	@Test
	void testGetSimulation() throws KsdServiceException {

		// Crypto currency not configured in DB
		when(cxCurrDao.findByCode("XXX")).thenReturn(null);
		assertThrows(KsdServiceException.class,
				() -> intradaySimulationService.getSimulation(buildIntraRq("XXX", null, null, null, null)));

		// Request dateTime is not valid (more than 24h)
		LocalDateTime rqDateTime = LocalDateTime.now().plusDays(2);
		CryptoCurrencyDto cxCurr = new CryptoCurrencyDto("AAA");
		when(cxCurrDao.findByCode("AAA")).thenReturn(cxCurr);
		assertThrows(KsdServiceException.class,
				() -> intradaySimulationService.getSimulation(buildIntraRq("AAA", "EUR", "M15", rqDateTime, null)));

		// Request dateTime is not provided
		LocalDateTime beginTime = LocalDateTime.now();
		intradaySimulationService.getSimulation(buildIntraRq("AAA", "EUR", "M15", null, null));
		verify(predictionDao).findBetweenDates(eq(cxCurr), captorDateFrom.capture(), captorDateTo.capture());
		assertTrue(ChronoUnit.SECONDS.between(beginTime, captorDateFrom.getValue()) < 10);
		assertEquals(24, ChronoUnit.HOURS.between(captorDateFrom.getValue(), captorDateTo.getValue()));

		// Request dateTime is provided
		beginTime = LocalDateTime.now().withSecond(0).withNano(0).plusHours(1);
		intradaySimulationService.getSimulation(buildIntraRq("AAA", "EUR", "M15", beginTime, null));
		verify(predictionDao).findBetweenDates(eq(cxCurr), eq(beginTime), captorDateTo.capture());
		assertEquals(24, ChronoUnit.HOURS.between(beginTime, captorDateTo.getValue()));

		// No prediction data stored in DB for the crypto currency
		when(predictionDao.findBetweenDates(eq(cxCurr), any(), any())).thenReturn(new ArrayList<PredictionDto>());
		IntradaySimulationResponse response = intradaySimulationService
				.getSimulation(buildIntraRq("AAA", "EUR", "M15", null, null));
		assertEquals("AAA", response.getCxCurr());
		assertEquals("EUR", response.getExCurr());
		assertTrue(response.getItems().isEmpty());

		// The crypto currency has prediction data stored in DB
		cxCurr = new CryptoCurrencyDto("BBB");
		when(cxCurrDao.findByCode("BBB")).thenReturn(cxCurr);
		List<PredictionDto> predictions = generatePredictions();
		when(predictionDao.findBetweenDates(eq(cxCurr), any(), any())).thenReturn(predictions);

		// No evaluated past predictions is stored in DB for the crypto currency
		when(predictionSuccessDao.findSuccess(cxCurr)).thenReturn(new ArrayList<PredictionSuccessDto>());
		String rqInterval = "M15";
		when(cxDataDao.getCurrVal(cxCurr)).thenReturn(4.58);
		response = intradaySimulationService.getSimulation(buildIntraRq("BBB", "EUR", rqInterval, null, 45.3));
		assertEquals("BBB", response.getCxCurr());
		assertEquals("EUR", response.getExCurr());
		assertNotNull(response.getItems());
		assertFalse(response.getItems().isEmpty());

		// All items has success="N/A" because there aren't evaluated past prediction
		assertTrue(response.getItems().stream().allMatch(item -> item.getSuccess().equals("N/A")
				&& item.getExpectedVal() != null && item.getDateTime() != null));

		// There are evaluated predictions
		when(predictionSuccessDao.findSuccess(cxCurr)).thenReturn(buildSuccessList(predictions));
		response = intradaySimulationService.getSimulation(buildIntraRq("BBB", "EUR", "M15", null, 50.7));
		assertNotNull(response.getItems());
		assertFalse(response.getItems().isEmpty());
		// All items has a percent value as success because there are evaluated past
		// predictions for all of them
		List<IntradaySimulationResponseItem> items = response.getItems();
		assertNotNull(items);
		assertFalse(items.isEmpty());
		assertTrue(items.stream().allMatch(item -> !item.getSuccess().equals("N/A") && item.getSuccess().contains("%")
				&& item.getExpectedVal() != null && item.getDateTime() != null && item.getProfit() != null));

		IntervalEnum interval = IntervalEnum.valueOf(rqInterval);

		Iterator<IntradaySimulationResponseItem> iter = items.iterator();
		IntradaySimulationResponseItem current, previous = iter.next();

		while (iter.hasNext()) {
			current = iter.next();

			// Minutes match interval values
			LocalDateTime prevDateTime = LocalDateTime.parse(previous.getDateTime(),
					DateTimeFormatter.ofPattern(DateUtils.FORMAT_DDMMYYYY_HHMM_2));
			assertNotNull(current.getDateTime());
			assertTrue(interval.getValues().contains(prevDateTime.getMinute()));

			// Items are sorted by date ascending
			LocalDateTime currDateTime = LocalDateTime.parse(current.getDateTime(),
					DateTimeFormatter.ofPattern(DateUtils.FORMAT_DDMMYYYY_HHMM_2));

			assertTrue(currDateTime.isAfter(prevDateTime));
			previous = current;
		}

	}

	private IntradaySimulationRequest buildIntraRq(String cxCurr, String exCurr, String interval,
			LocalDateTime rqDateTime, Double amount) {
		IntradaySimulationRequest request = new IntradaySimulationRequest();

		request.setCxCurr(cxCurr);
		request.setExCurr(exCurr);
		request.setInterval(interval);
		if (rqDateTime != null) {
			DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern(DateUtils.FORMAT_DDMMYYYY_HHMM);
			request.setDateTime(rqDateTime.format(dtFormat));
		}
		request.setAmount(amount);

		return request;
	}

	private List<PredictionDto> generatePredictions() {
		List<PredictionDto> items = new ArrayList<>();
		Random r = new Random();
		LocalDateTime currTime = LocalDateTime.now().withMinute(0);

		for (int i = 0; i < 96; i++) {
			PredictionDto dto = new PredictionDto();
			dto.setId(i);
			dto.setAdvance(r.nextInt());
			dto.setCurrTime(currTime);
			dto.setPredictTime(currTime.plusMinutes(i * 15).withSecond(0));
			dto.setPredictVal(r.nextDouble());
			dto.setSampleSize(r.nextInt());
			dto.setSuccess(null);
			items.add(dto);
		}

		return items;
	}

	private List<PredictionSuccessDto> buildSuccessList(List<PredictionDto> predictions) {
		List<PredictionSuccessDto> items = new ArrayList<>();
		Random r = new Random();

		// Simply to make sure that all samples and advances from predictions exist in
		// predictionSuccess
		for (PredictionDto prediction : predictions) {
			PredictionSuccessDto item = new PredictionSuccessDto();
			item.setAdvance(prediction.getAdvance());
			item.setSampleSize(prediction.getSampleSize());
			item.setSuccess(r.nextDouble());
			items.add(item);
		}

		return items;
	}

}
