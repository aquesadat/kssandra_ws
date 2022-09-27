/**
 * 
 */
package com.kssandra.ksd_ws.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.kssandra.ksd_common.dto.CryptoCurrencyDto;
import com.kssandra.ksd_common.dto.PredictionDto;
import com.kssandra.ksd_common.dto.PredictionSuccessDto;
import com.kssandra.ksd_common.util.DateUtils;
import com.kssandra.ksd_persistence.dao.CryptoCurrencyDao;
import com.kssandra.ksd_persistence.dao.PredictionDao;
import com.kssandra.ksd_persistence.dao.PredictionSuccessDao;
import com.kssandra.ksd_ws.enums.IntervalEnum;
import com.kssandra.ksd_ws.exception.KsdServiceException;
import com.kssandra.ksd_ws.request.IntradayPredictionRequest;
import com.kssandra.ksd_ws.response.IntradayPredictionResponse;
import com.kssandra.ksd_ws.response.IntradayPredictionResponseItem;

/**
 * * Test class for IntradayPredictionService
 * 
 * @author aquesada
 *
 */
@SpringBootTest
class IntradayPredictionServiceTest {

	@MockBean
	CryptoCurrencyDao cxCurrDao;

	@MockBean
	PredictionDao predictionDao;

	@Autowired
	IntradayPredictionService intradayPredictionService;

	@MockBean
	PredictionSuccessDao predictionSuccessDao;

	/**
	 * Test method for
	 * {@link com.kssandra.ksd_ws.service.IntradayPredictionService#getPrediction(com.kssandra.ksd_ws.request.IntradayPredictionRequest)}.
	 * 
	 * @throws KsdServiceException
	 */
	@Test
	void testGetPrediction() throws KsdServiceException {

		// Crypto currency not configured in DB
		when(cxCurrDao.findByCode("XXX")).thenReturn(null);
		assertThrows(KsdServiceException.class,
				() -> intradayPredictionService.getPrediction(buildIntraRq("XXX", null, null)));

		// No prediction data stored in DB for the crypto currency
		CryptoCurrencyDto cxCurr = new CryptoCurrencyDto("AAA");
		when(cxCurrDao.findByCode("AAA")).thenReturn(cxCurr);
		when(predictionDao.findBetweenDates(eq(cxCurr), any(), any())).thenReturn(new ArrayList<PredictionDto>());
		IntradayPredictionResponse response = intradayPredictionService
				.getPrediction(buildIntraRq("AAA", "EUR", "M15"));
		assertEquals("AAA", response.getCxCurr());
		assertEquals("EUR", response.getExCurr());
		assertTrue(response.getItems().isEmpty());

		// The crypto currency has prediction data stored in DB
		cxCurr = new CryptoCurrencyDto("BBB");
		when(cxCurrDao.findByCode("BBB")).thenReturn(cxCurr);
		List<PredictionDto> predictions = buildItemList();
		when(predictionDao.findBetweenDates(eq(cxCurr), any(), any())).thenReturn(predictions);

		// No evaluated past predictions is stored in DB for the crypto currency
		when(predictionSuccessDao.findSuccess(cxCurr)).thenReturn(new ArrayList<PredictionSuccessDto>());
		String rqInterval = "M15";
		response = intradayPredictionService.getPrediction(buildIntraRq("BBB", "EUR", rqInterval));
		assertEquals("BBB", response.getCxCurr());
		assertEquals("EUR", response.getExCurr());
		assertNotNull(response.getItems());
		assertFalse(response.getItems().isEmpty());

		// All items has success="N/A" because there aren't evaluated past prediction
		assertTrue(response.getItems().stream().allMatch(item -> item.getSuccess().equals("N/A")
				&& item.getExpectedVal() != null && item.getDateTime() != null));

		// There are evaluated predictions
		when(predictionSuccessDao.findSuccess(cxCurr)).thenReturn(buildSuccessList(predictions));
		response = intradayPredictionService.getPrediction(buildIntraRq("BBB", "EUR", "M15"));
		assertNotNull(response.getItems());
		assertFalse(response.getItems().isEmpty());
		// All items has a percent value as success because there are evaluated past
		// predictions for all of them
		List<IntradayPredictionResponseItem> items = response.getItems();
		assertNotNull(items);
		assertFalse(items.isEmpty());
		assertTrue(items.stream().allMatch(item -> !item.getSuccess().equals("N/A") && item.getSuccess().contains("%")
				&& item.getExpectedVal() != null && item.getDateTime() != null));

		IntervalEnum interval = IntervalEnum.valueOf(rqInterval);

		Iterator<IntradayPredictionResponseItem> iter = items.iterator();
		IntradayPredictionResponseItem current, previous = iter.next();

		while (iter.hasNext()) {
			current = iter.next();

			// Minutes match interval values
			LocalDateTime prevDateTime = LocalDateTime.parse(previous.getDateTime(),
					DateTimeFormatter.ofPattern(DateUtils.FORMAT_DDMMYYYY_HHMMSS));
			assertNotNull(current.getDateTime());
			assertTrue(interval.getValues().contains(prevDateTime.getMinute()));

			// Items are sorted by date ascending
			LocalDateTime currDateTime = LocalDateTime.parse(current.getDateTime(),
					DateTimeFormatter.ofPattern(DateUtils.FORMAT_DDMMYYYY_HHMMSS));

			assertTrue(currDateTime.isAfter(prevDateTime));
			previous = current;
		}

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

	private List<PredictionDto> buildItemList() {
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

	private IntradayPredictionRequest buildIntraRq(String cxCurr, String exCurr, String interval) {
		IntradayPredictionRequest request = new IntradayPredictionRequest();

		request.setCxCurr(cxCurr);
		request.setExCurr(exCurr);
		request.setInterval(interval);

		return request;
	}

}
