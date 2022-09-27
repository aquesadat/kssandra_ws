package com.kssandra.ksd_ws.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import com.kssandra.ksd_persistence.dao.CryptoDataDao;
import com.kssandra.ksd_persistence.dao.PredictionDao;
import com.kssandra.ksd_persistence.dao.PredictionSuccessDao;
import com.kssandra.ksd_ws.enums.IntervalEnum;
import com.kssandra.ksd_ws.exception.KsdServiceException;
import com.kssandra.ksd_ws.request.IntradaySimulationRequest;
import com.kssandra.ksd_ws.response.IntradaySimulationResponse;
import com.kssandra.ksd_ws.response.IntradaySimulationResponseItem;
import com.kssandra.ksd_ws.util.PredictionUtil;

/**
 * Service class for /intraday/simulate endpoint.
 * 
 * @author aquesada
 *
 */
@Service
public class IntradaySimulationService {

	/** The CryptoCurrency DAO. */
	@Autowired
	CryptoCurrencyDao cxCurrDao;

	/** The Prediction DAO. */
	@Autowired
	PredictionDao predictionDao;

	/** The CryptoData DAO. */
	@Autowired
	CryptoDataDao cxDataDao;

	/** The PredictionSuccess DAO. */
	@Autowired
	PredictionSuccessDao predictionSuccessDao;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(IntradaySimulationService.class);

	/**
	 * Gets the simulation.
	 *
	 * @param intraRq the request
	 * @return the simulation
	 * @throws KsdServiceException the ksd service exception
	 */
	public IntradaySimulationResponse getSimulation(IntradaySimulationRequest intraRq) throws KsdServiceException {

		IntradaySimulationResponse response = null;

		LOG.debug("Getting simulation for cx currency {}", intraRq.getCxCurr());

		CryptoCurrencyDto cxCurrDto = cxCurrDao.findByCode(intraRq.getCxCurr());
		if (cxCurrDto != null) {

			boolean hasDate = StringUtils.isNotBlank(intraRq.getDateTime());

			List<PredictionDto> predictions = null;

			// Get 24h of predictions from given date. If no date is provided, take current
			// date as begin.
			if (hasDate) {
				LocalDateTime rqDate = LocalDateTime.parse(intraRq.getDateTime(),
						DateTimeFormatter.ofPattern(DateUtils.FORMAT_DDMMYYYY_HHMM));
				if (rqDate.isAfter(LocalDateTime.now().plusDays(1)) || rqDate.isBefore(LocalDateTime.now())) {
					throw new KsdServiceException("dateTime must be in the next 24h");
				}
				predictions = predictionDao.findBetweenDates(cxCurrDto, rqDate, rqDate.plusDays(1));
			} else {
				predictions = predictionDao.findBetweenDates(cxCurrDto, LocalDateTime.now(),
						LocalDateTime.now().plusDays(1));
			}

			IntervalEnum interval = IntervalEnum.fromName(intraRq.getInterval());

			response = new IntradaySimulationResponse();
			response.setCxCurr(intraRq.getCxCurr());
			response.setExCurr(intraRq.getExCurr());

			response.setItems(getItems(predictions, interval, intraRq.getAmount(), intraRq.getPurchaseFee(),
					intraRq.getSaleFee(), intraRq.getDateTime(), cxCurrDto));

		} else {
			throw new KsdServiceException("No cxcurrency found in DB for code: ".concat(intraRq.getCxCurr()));
		}

		return response;
	}

	/**
	 * Builds the response list of items.
	 *
	 * @param predictions the predictions
	 * @param interval    the interval
	 * @param amount      the amount
	 * @param purchaseFee the purchase fee
	 * @param saleFee     the sale fee
	 * @param dateTime    the date time
	 * @param cxCurrDto   the crypto currency dto
	 * @return the items
	 */
	private List<IntradaySimulationResponseItem> getItems(List<PredictionDto> predictions, IntervalEnum interval,
			Double amount, Double purchaseFee, Double saleFee, String dateTime, CryptoCurrencyDto cxCurrDto) {
		List<IntradaySimulationResponseItem> items = new ArrayList<>();

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
			Double currVal = cxDataDao.getCurrVal(cxCurrDto);
			bestPredictions.values().stream().sorted((e1, e2) -> e1.getPredictTime().compareTo(e2.getPredictTime()))
					.forEach(pred -> items.add(predToItem(pred, amount, purchaseFee, saleFee, dateTime, currVal)));
		} else {
			LOG.warn("No prediction was found");
		}

		return items;
	}

	/**
	 * Builds the response item
	 *
	 * @param predictionDto the predictionDto
	 * @param amount        the amount
	 * @param purchaseFee   the purchase fee
	 * @param saleFee       the sale fee
	 * @param dateTime      the date time
	 * @param currVal       the current value
	 * @return the IntradaySimulationResponseItem
	 */
	private IntradaySimulationResponseItem predToItem(PredictionDto predictionDto, Double amount, Double purchaseFee,
			Double saleFee, String dateTime, Double currVal) {
		IntradaySimulationResponseItem item = new IntradaySimulationResponseItem();

		item.setDateTime(dateTime != null ? dateTime
				: predictionDto.getPredictTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
		item.setSuccess(PredictionUtil.beautifySuccess(predictionDto.getSuccess()));
		item.setExpectedVal(PriceUtils.roundPrice(amount / currVal * predictionDto.getPredictVal()));

		saleFee = (saleFee == null || saleFee == 0) ? 1 : (100 - saleFee) / 100;
		purchaseFee = (purchaseFee == null || purchaseFee == 0) ? 1 : (100 + purchaseFee) / 100;
		item.setProfit(PriceUtils.roundPrice((item.getExpectedVal() * saleFee) - (amount * purchaseFee)));

		return item;
	}

}
