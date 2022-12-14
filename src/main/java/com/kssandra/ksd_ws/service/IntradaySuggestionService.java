package com.kssandra.ksd_ws.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kssandra.ksd_common.dto.CryptoCurrencyDto;
import com.kssandra.ksd_common.dto.PredictionDto;
import com.kssandra.ksd_common.dto.PredictionSuccessDto;
import com.kssandra.ksd_common.logger.KSDLoggerFactory;
import com.kssandra.ksd_common.util.PriceUtils;
import com.kssandra.ksd_persistence.dao.CryptoCurrencyDao;
import com.kssandra.ksd_persistence.dao.CryptoDataDao;
import com.kssandra.ksd_persistence.dao.PredictionDao;
import com.kssandra.ksd_persistence.dao.PredictionSuccessDao;
import com.kssandra.ksd_ws.request.IntradaySuggestionRequest;
import com.kssandra.ksd_ws.response.IntradaySuggestionResponse;
import com.kssandra.ksd_ws.response.IntradaySuggestionResponseItem;
import com.kssandra.ksd_ws.util.PredictionUtil;

/**
 * Service class for /intraday/suggest endpoint.
 *
 * @author aquesada
 */
@Service
public class IntradaySuggestionService {

	/** Max number of cxcurrs in the response */
	public static final int MAX_RESULTS = 10;

	/** The CrptoCurrency DAO. */
	@Autowired
	CryptoCurrencyDao cxCurrDao;

	/** The CryptoData DAO */
	@Autowired
	CryptoDataDao cxDataDao;

	/** The Prediction DAO. */
	@Autowired
	PredictionDao predictionDao;

	/** The PredictionSuccess DAO. */
	@Autowired
	PredictionSuccessDao predictionSuccessDao;

	/** The intraday prediction service. */
	@Autowired
	IntradayPredictionService intradayPredService;

	/** The Constant LOG. */
	private static final Logger LOG = KSDLoggerFactory.getLogger();

	/**
	 * Gets the suggestion.
	 *
	 * @param intraRq the request
	 * @return the suggestion
	 */
	public IntradaySuggestionResponse getSuggestion(IntradaySuggestionRequest intraRq) {

		IntradaySuggestionResponse response = new IntradaySuggestionResponse();

		List<CryptoCurrencyDto> cxCurrs = cxCurrDao.getAllActiveCxCurrencies();
		Map<Double, List<IntradaySuggestionResponseItem>> suggItems = new TreeMap<>(Collections.reverseOrder());

		LOG.debug("Found {} active cryptocurrencies", cxCurrs.size());

		for (CryptoCurrencyDto cxCurr : cxCurrs) {

			// Get predictions for the current cryptocurrency and time = +24h
			List<PredictionDto> predictions = predictionDao.findByDate(cxCurr, LocalDateTime.now().plusDays(1));

			// Gets (from a view) all sample-advance combinations and its success (data from
			// past predictions already evaluated)
			List<PredictionSuccessDto> predSuccess = predictionSuccessDao.findSuccess(cxCurr);
			LOG.debug("{} success predictions found", predSuccess.size());

			// According to the past evaluated predictions (predSuccess), get the best
			// future prediction (sample-advance combination) for every time in the next 24h
			Collection<PredictionDto> bestPredictions = PredictionUtil
					.getBestPredictions(predictions, predSuccess, null).values();
			LOG.debug("{} best predictions found", bestPredictions.size());

			// Get the best success (or the highest price) from all the predictions
			Optional<PredictionDto> bestPrediction;
			if (bestPredictions.stream().anyMatch(bp -> bp.getSuccess() != null)) {
				bestPrediction = bestPredictions.stream().filter(bp -> bp.getSuccess() != null)
						.sorted((e1, e2) -> e2.getSuccess().compareTo(e1.getSuccess())).findFirst();
			} else {
				bestPrediction = bestPredictions.stream()
						.sorted((e1, e2) -> e1.getPredictTime().compareTo(e2.getPredictTime())).findFirst();
				LOG.warn("All predictions without success value");
			}

			// Builds a map <raise, prediction> sorted by raise descending to get the most
			// potential price raises
			bestPrediction.ifPresentOrElse(bp -> addPrediction(suggItems, cxCurr, bp),
					() -> LOG.error("Best prediction not found for cxCurr {}", cxCurr.getCode()));
		}

		long nResults = intraRq.getNumResult() != null ? intraRq.getNumResult() : MAX_RESULTS;

		List<IntradaySuggestionResponseItem> items = suggItems.values().stream().flatMap(List::stream).limit(nResults)
				.collect(Collectors.toList());

		for (int i = 0; i < items.size(); i++) {
			items.get(i).setRank(i + 1);
		}

		response.setItems(items);
		response.setExCurr(intraRq.getExCurr());

		return response;
	}

	/**
	 * Adds the prediction to a map with price raise as key
	 *
	 * @param suggItems      the sugg items
	 * @param cxCurr         the cx curr
	 * @param bestPrediction the best prediction
	 */
	private void addPrediction(Map<Double, List<IntradaySuggestionResponseItem>> suggItems, CryptoCurrencyDto cxCurr,
			PredictionDto bestPrediction) {
		Double currVal = cxDataDao.getCurrVal(cxCurr);

		IntradaySuggestionResponseItem item = new IntradaySuggestionResponseItem();

		Double raise = PriceUtils.roundPrice(((bestPrediction.getPredictVal() * 100) / currVal) - 100);
		item.setCxCurr(cxCurr.getCode());
		item.setCxCurrDesc(cxCurr.getName());
		item.setExpectedRaise(String.valueOf(raise).concat("%"));
		item.setExpectedVal(PriceUtils.roundPrice(bestPrediction.getPredictVal()));
		item.setSuccess(PredictionUtil.beautifySuccess(bestPrediction.getSuccess()));
		item.setCurrVal(PriceUtils.roundPrice(currVal));

		if (suggItems.containsKey(raise)) {
			suggItems.get(raise).add(item);
		} else {
			suggItems.put(raise, new ArrayList<>(List.of(item)));
		}

	}

}
