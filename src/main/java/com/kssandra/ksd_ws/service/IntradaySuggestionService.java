package com.kssandra.ksd_ws.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kssandra.ksd_common.dto.CryptoCurrencyDto;
import com.kssandra.ksd_common.dto.PredictionDto;
import com.kssandra.ksd_common.dto.PredictionSuccessDto;
import com.kssandra.ksd_common.util.PriceUtils;
import com.kssandra.ksd_persistence.dao.CryptoCurrencyDao;
import com.kssandra.ksd_persistence.dao.CryptoDataDao;
import com.kssandra.ksd_persistence.dao.PredictionDao;
import com.kssandra.ksd_persistence.dao.PredictionSuccessDao;
import com.kssandra.ksd_ws.exception.KsdServiceException;
import com.kssandra.ksd_ws.request.IntradaySuggestionRequest;
import com.kssandra.ksd_ws.response.IntradaySuggestionResponse;
import com.kssandra.ksd_ws.response.IntradaySuggestionResponseItem;
import com.kssandra.ksd_ws.util.PredictionUtil;

@Service
public class IntradaySuggestionService {

	public static final long MAX_RESULTS = 5;

	@Autowired
	CryptoCurrencyDao cxCurrDao;

	@Autowired
	CryptoDataDao cxDataDao;

	@Autowired
	PredictionDao predictionDao;

	@Autowired
	PredictionSuccessDao predictionSuccessDao;

	@Autowired
	IntradayPredictionService intradayPredService;

	private static final Logger LOG = LoggerFactory.getLogger(IntradaySuggestionService.class);

	public IntradaySuggestionResponse getSuggestion(@Valid IntradaySuggestionRequest intraRq)
			throws KsdServiceException {

		IntradaySuggestionResponse response = new IntradaySuggestionResponse();

		List<CryptoCurrencyDto> cxCurrs = cxCurrDao.getAllActiveCxCurrencies();
		Map<Double, IntradaySuggestionResponseItem> suggItems = new TreeMap<>(Collections.reverseOrder());

		for (CryptoCurrencyDto cxCurr : cxCurrs) {

			List<PredictionDto> predictions = predictionDao.findByDate(cxCurr, LocalDateTime.now().plusDays(1));

			List<PredictionSuccessDto> predSuccess = predictionSuccessDao
					.findSuccess(predictions.get(0).getCxCurrencyDto());

			Map<LocalDateTime, PredictionDto> bestPredictions = PredictionUtil.getBestPredictions(predictions,
					predSuccess, null);

			Optional<PredictionDto> bestPrediction = bestPredictions.values().stream()
					.sorted((e1, e2) -> e1.getPredictTime().compareTo(e2.getPredictTime())).findFirst();

			if (bestPrediction.isPresent()) {
				addPrediction(suggItems, cxCurr, bestPrediction.get());
			} else {
				LOG.error("Best prediction not found for cxCurr {}", cxCurr.getCode());
			}
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

	private void addPrediction(Map<Double, IntradaySuggestionResponseItem> suggItems, CryptoCurrencyDto cxCurr,
			PredictionDto bestPrediction) {
		Double currVal = cxDataDao.getCurrVal(cxCurr);

		IntradaySuggestionResponseItem item = new IntradaySuggestionResponseItem();

		Double raise = PriceUtils.roundPrice(100 - (bestPrediction.getPredictVal() * 100) / currVal);
		item.setCxCurr(cxCurr.getCode());
		item.setExpectedRaise(String.valueOf(raise).concat("%"));
		item.setExpectedVal(PriceUtils.roundPrice(bestPrediction.getPredictVal()));
		item.setSuccess(PredictionUtil.beautifySuccess(bestPrediction.getSuccess()));

		suggItems.put(raise, item);

	}

}
