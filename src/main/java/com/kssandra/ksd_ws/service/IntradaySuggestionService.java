package com.kssandra.ksd_ws.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kssandra.ksd_common.dto.CryptoCurrencyDto;
import com.kssandra.ksd_common.util.PriceUtils;
import com.kssandra.ksd_persistence.dao.CryptoCurrencyDao;
import com.kssandra.ksd_persistence.dao.CryptoDataDao;
import com.kssandra.ksd_ws.enums.IntervalEnum;
import com.kssandra.ksd_ws.exception.KsdServiceException;
import com.kssandra.ksd_ws.request.IntradayPredictionRequest;
import com.kssandra.ksd_ws.request.IntradaySuggestionRequest;
import com.kssandra.ksd_ws.response.IntradayPredictionResponse;
import com.kssandra.ksd_ws.response.IntradayPredictionResponseItem;
import com.kssandra.ksd_ws.response.IntradaySuggestionResponse;
import com.kssandra.ksd_ws.response.IntradaySuggestionResponseItem;

@Service
public class IntradaySuggestionService {

	public static final long MAX_RESULTS = 5;

	@Autowired
	CryptoCurrencyDao cxCurrDao;

	@Autowired
	CryptoDataDao cxDataDao;

	@Autowired
	IntradayPredictionService intradayPredService;

	public IntradaySuggestionResponse getSuggestion(@Valid IntradaySuggestionRequest intraRq)
			throws KsdServiceException {

		IntradaySuggestionResponse response = new IntradaySuggestionResponse();

		List<CryptoCurrencyDto> cxCurrs = cxCurrDao.getAllActiveCxCurrencies();

		IntradayPredictionRequest intraPredRq = new IntradayPredictionRequest();
		intraPredRq.setExCurr(intraRq.getExCurr());
		intraPredRq.setInterval(IntervalEnum.M60.getName());

		Map<Double, IntradaySuggestionResponseItem> suggItems = new TreeMap<>(Collections.reverseOrder());
		for (CryptoCurrencyDto cxCurr : cxCurrs) {
			intraPredRq.setCxCurr(cxCurr.getCode());
			IntradayPredictionResponse predictionRs = intradayPredService.getPrediction(intraPredRq);
			addPrediction(suggItems, predictionRs.getItems().get(predictionRs.getItems().size() - 1),
					cxDataDao.getCurrVal(cxCurr), cxCurr.getCode());
		}

		long nResults = intraRq.getNumResult() != null ? intraRq.getNumResult() : MAX_RESULTS;

		List<IntradaySuggestionResponseItem> items = suggItems.values().stream().limit(nResults)
				.collect(Collectors.toList());

		for (int i = 0; i < items.size(); i++) {
			items.get(i).setRank(i + 1);
		}

		response.setItems(items);
		response.setExCurr(intraRq.getExCurr());

		return response;
	}

	private void addPrediction(Map<Double, IntradaySuggestionResponseItem> predItems,
			IntradayPredictionResponseItem predItem, Double currVal, String cxCurr) {

		IntradaySuggestionResponseItem item = new IntradaySuggestionResponseItem();

		Double raise = PriceUtils.roundPrice(100 - (predItem.getExpectedVal() * 100) / currVal);
		item.setCxCurr(cxCurr);
		item.setExpectedRaise(String.valueOf(raise).concat("%"));
		item.setExpectedVal(predItem.getExpectedVal());
		item.setSuccess(predItem.getSuccess());

		predItems.put(raise, item);
	}

}
