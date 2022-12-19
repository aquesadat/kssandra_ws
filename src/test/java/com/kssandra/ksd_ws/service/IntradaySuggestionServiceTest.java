/**
 * 
 */
package com.kssandra.ksd_ws.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.kssandra.ksd_common.dto.CryptoCurrencyDto;
import com.kssandra.ksd_common.dto.PredictionDto;
import com.kssandra.ksd_common.dto.PredictionSuccessDto;
import com.kssandra.ksd_persistence.dao.CryptoCurrencyDao;
import com.kssandra.ksd_persistence.dao.CryptoDataDao;
import com.kssandra.ksd_persistence.dao.PredictionDao;
import com.kssandra.ksd_persistence.dao.PredictionSuccessDao;
import com.kssandra.ksd_ws.enums.ExchangeCurrEnum;
import com.kssandra.ksd_ws.request.IntradaySuggestionRequest;
import com.kssandra.ksd_ws.response.IntradaySuggestionResponse;
import com.kssandra.ksd_ws.response.IntradaySuggestionResponseItem;

/**
 * Test class for IntradaySuggestionService
 * 
 * @author aquesada
 *
 */
@SpringBootTest
class IntradaySuggestionServiceTest {

	/** The Prediction DAO. */
	@MockBean
	PredictionDao predictionDao;

	@MockBean
	CryptoCurrencyDao cxCurrDao;

	@MockBean
	PredictionSuccessDao predictionSuccessDao;

	@MockBean
	CryptoDataDao cxDataDao;

	/** The intraday prediction service. */
	@Autowired
	IntradaySuggestionService intradaySuggestionService;

	/**
	 * Test method for
	 * {@link com.kssandra.ksd_ws.service.IntradaySuggestionService#getSuggestion(com.kssandra.ksd_ws.request.IntradaySuggestionRequest)}.
	 */
	@Test
	void testGetSuggestion() {

		// No currencies configured as active
		when(cxCurrDao.getAllActiveCxCurrencies()).thenReturn(new ArrayList<CryptoCurrencyDto>());
		IntradaySuggestionResponse response = intradaySuggestionService
				.getSuggestion(buildIntraRq(ExchangeCurrEnum.EUR.getValue(), null));
		assertTrue(response.getItems().isEmpty());

		// No prediction data stored in DB for the crypto currency
		CryptoCurrencyDto cxCurrA = new CryptoCurrencyDto("AAA", null, null, false);
		CryptoCurrencyDto cxCurrB = new CryptoCurrencyDto("BBB", null, null, false);
		CryptoCurrencyDto cxCurrC = new CryptoCurrencyDto("CCC", null, null, false);
		List<CryptoCurrencyDto> cxCurrs = new ArrayList<>();
		cxCurrs.add(cxCurrA);
		cxCurrs.add(cxCurrB);
		cxCurrs.add(cxCurrC);
		when(cxCurrDao.getAllActiveCxCurrencies()).thenReturn(cxCurrs);
		when(predictionDao.findByDate(any(), any())).thenReturn(new ArrayList<PredictionDto>());
		response = intradaySuggestionService.getSuggestion(buildIntraRq(ExchangeCurrEnum.EUR.getValue(), null));
		assertEquals("EUR", response.getExCurr());
		assertTrue(response.getItems().isEmpty());

		// The crypto currencies have prediction data stored in DB
		when(predictionDao.findByDate(eq(cxCurrA), any())).thenReturn(generatePredictions(cxCurrA));
		when(predictionDao.findByDate(eq(cxCurrB), any())).thenReturn(generatePredictions(cxCurrB));
		when(predictionDao.findByDate(eq(cxCurrC), any())).thenReturn(generatePredictions(cxCurrC));

		// No evaluated past predictions is stored in DB for the crypto currency
		when(predictionSuccessDao.findSuccess(cxCurrA)).thenReturn(new ArrayList<PredictionSuccessDto>());
		when(predictionSuccessDao.findSuccess(cxCurrB)).thenReturn(new ArrayList<PredictionSuccessDto>());
		when(predictionSuccessDao.findSuccess(cxCurrC)).thenReturn(new ArrayList<PredictionSuccessDto>());
		response = intradaySuggestionService.getSuggestion(buildIntraRq(ExchangeCurrEnum.EUR.getValue(), null));
		assertEquals("EUR", response.getExCurr());
		assertNotNull(response.getItems());
		assertFalse(response.getItems().isEmpty());

		// There are evaluated predictions
		when(predictionSuccessDao.findSuccess(cxCurrA))
				.thenReturn(buildSuccessList(generatePredictions(cxCurrA), cxCurrA));
		when(predictionSuccessDao.findSuccess(cxCurrB))
				.thenReturn(buildSuccessList(generatePredictions(cxCurrB), cxCurrB));
		when(predictionSuccessDao.findSuccess(cxCurrC))
				.thenReturn(buildSuccessList(generatePredictions(cxCurrC), cxCurrC));

		when(cxDataDao.getCurrVal(cxCurrA)).thenReturn(4.58);
		when(cxDataDao.getCurrVal(cxCurrB)).thenReturn(5.03);
		when(cxDataDao.getCurrVal(cxCurrC)).thenReturn(10.80);
		response = intradaySuggestionService.getSuggestion(buildIntraRq(ExchangeCurrEnum.EUR.getValue(), null));
		assertNotNull(response.getItems());
		assertFalse(response.getItems().isEmpty());
		assertEquals(3, response.getItems().size());
		// All items has a percent value as success because there are evaluated past
		// predictions for all of them
		List<IntradaySuggestionResponseItem> items = response.getItems();
		assertNotNull(items);
		assertFalse(items.isEmpty());
		assertTrue(items.stream()
				.allMatch(item -> (item.getSuccess().equals("N/A") || item.getSuccess().contains("%"))
						&& item.getExpectedVal() != null && item.getCxCurr() != null && item.getExpectedRaise() != null
						&& item.getRank() > 0));

		Iterator<IntradaySuggestionResponseItem> iter = items.iterator();
		IntradaySuggestionResponseItem current, previous = iter.next();

		while (iter.hasNext()) {
			current = iter.next();
			assertTrue(current.getRank() > previous.getRank());
			previous = current;
		}

		// Specified numResult < Configured cxCurrs
		response = intradaySuggestionService.getSuggestion(buildIntraRq(ExchangeCurrEnum.EUR.getValue(), 2));
		assertEquals(2, response.getItems().size());

		// Specified numResult > Configured cxCurrs
		response = intradaySuggestionService.getSuggestion(buildIntraRq(ExchangeCurrEnum.EUR.getValue(), 7));
		assertEquals(3, response.getItems().size());
	}

	private IntradaySuggestionRequest buildIntraRq(String exCurr, Integer numResult) {
		IntradaySuggestionRequest request = new IntradaySuggestionRequest();

		request.setExCurr(exCurr);
		request.setNumResult(numResult);

		return request;
	}

	private List<PredictionDto> generatePredictions(CryptoCurrencyDto cxCurr) {
		List<PredictionDto> items = new ArrayList<>();
		RandomGenerator r = RandomGeneratorFactory.getDefault().create();

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
			dto.setCxCurrencyDto(cxCurr);
			items.add(dto);
		}

		return items;
	}

	private List<PredictionSuccessDto> buildSuccessList(List<PredictionDto> predictions, CryptoCurrencyDto cxCurr) {
		List<PredictionSuccessDto> items = new ArrayList<>();
		RandomGenerator r = RandomGeneratorFactory.getDefault().create();

		// Simply to make sure that all samples and advances from predictions exist in
		// predictionSuccess
		for (PredictionDto prediction : predictions) {
			PredictionSuccessDto item = new PredictionSuccessDto(cxCurr, prediction.getSampleSize(),
					prediction.getAdvance(), r.nextDouble());
			items.add(item);
		}

		return items;
	}

}
