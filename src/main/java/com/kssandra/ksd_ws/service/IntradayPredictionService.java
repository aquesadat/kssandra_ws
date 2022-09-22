package com.kssandra.ksd_ws.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kssandra.ksd_common.dto.CryptoCurrencyDto;
import com.kssandra.ksd_common.dto.PredictionDto;
import com.kssandra.ksd_common.dto.PredictionSuccessDto;
import com.kssandra.ksd_common.util.DateUtils;
import com.kssandra.ksd_common.util.PriceUtils;
import com.kssandra.ksd_persistence.dao.CryptoCurrencyDao;
import com.kssandra.ksd_persistence.dao.PredictionDao;
import com.kssandra.ksd_persistence.dao.PredictionSuccessDao;
import com.kssandra.ksd_ws.enums.IntervalEnum;
import com.kssandra.ksd_ws.exception.KsdServiceException;
import com.kssandra.ksd_ws.request.IntradayPredictionRequest;
import com.kssandra.ksd_ws.response.IntradayPredictionResponse;
import com.kssandra.ksd_ws.response.IntradayPredictionResponseItem;
import com.kssandra.ksd_ws.util.PredictionUtil;

/**
 * Service class for /intraday/prediction endpoint.
 *
 * @author aquesada
 */
@Service
public class IntradayPredictionService {

	/** The Prediction DAO. */
	@Autowired
	PredictionDao predictionDao;

	/** The PredictionSuccess DAO. */
	@Autowired
	PredictionSuccessDao predictionSuccessDao;

	/** The CryptoCurrency DAO. */
	@Autowired
	CryptoCurrencyDao cxCurrDao;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(IntradayPredictionService.class);

	/**
	 * Gets the prediction.
	 *
	 * @param intraRq the intra rq
	 * @return the prediction
	 * @throws KsdServiceException the ksd service exception
	 */
	public IntradayPredictionResponse getPrediction(IntradayPredictionRequest intraRq) throws KsdServiceException {
		IntradayPredictionResponse response = null;

		LOG.debug("Getting predictions for cx currency {}", intraRq.getCxCurr());
		CryptoCurrencyDto cxCurrDto = cxCurrDao.findByCode(intraRq.getCxCurr());

		if (cxCurrDto != null) {

			// Gets future price predictions for the next 24h
			List<PredictionDto> predictions = predictionDao.findAfterDate(cxCurrDto, LocalDateTime.now(),
					LocalDateTime.now().plusDays(1));
			LOG.info("{} predictions found", predictions.size());

			IntervalEnum interval = IntervalEnum.fromName(intraRq.getInterval());

			response = new IntradayPredictionResponse();
			response.setCxCurr(intraRq.getCxCurr());
			response.setExCurr(intraRq.getExCurr());
			response.setItems(getItems(predictions, interval, cxCurrDto));

		} else {
			LOG.error("Cx currency not found");
			throw new KsdServiceException("Any cxcurrency found in DB for code: ".concat(intraRq.getCxCurr()));
		}

		return response;
	}

	/**
	 * Builds the response list of items.
	 *
	 * @param predictions future prices predicted
	 * @param interval    the interval
	 * @param cxCurrDto   the crypto currency
	 * @return the list of response items
	 */
	private List<IntradayPredictionResponseItem> getItems(List<PredictionDto> predictions, IntervalEnum interval,
			CryptoCurrencyDto cxCurrDto) {
		List<IntradayPredictionResponseItem> items = new ArrayList<>();

		if (!predictions.isEmpty()) {
			// Gets (from a view) all sample-advance combinations and its success (data from
			// past predictions already evaluated)
			List<PredictionSuccessDto> predSuccess = predictionSuccessDao.findSuccess(cxCurrDto);
			LOG.debug("{} success predictions found", predSuccess.size());

			// According to the past evaluated predictions (predSuccess), get the best
			// future prediction (sample-advance combination) for every time in the next 24h
			Map<LocalDateTime, PredictionDto> bestPredictions = PredictionUtil.getBestPredictions(predictions,
					predSuccess, interval);
			LOG.debug("{} best predictions found", bestPredictions.size());

			// Sorts predictions by time and builds the final response item
			bestPredictions.values().stream().sorted((e1, e2) -> e1.getPredictTime().compareTo(e2.getPredictTime()))
					.forEach(pred -> items.add(predToItem(pred)));
		} else {
			LOG.warn("Any prediction was found");
		}

		return items;
	}

	/**
	 * Builds the response item
	 *
	 * @param dto the prediction
	 * @return the response item
	 */
	private IntradayPredictionResponseItem predToItem(PredictionDto dto) {
		IntradayPredictionResponseItem item = new IntradayPredictionResponseItem();

		item.setExpectedVal(PriceUtils.roundPrice(dto.getPredictVal()));
		item.setSuccess(PredictionUtil.beautifySuccess(dto.getSuccess()));
		item.setDateTime(dto.getPredictTime().format(DateTimeFormatter.ofPattern(DateUtils.FORMAT_DDMMYYYY_HHMMSS)));

		return item;
	}

}
