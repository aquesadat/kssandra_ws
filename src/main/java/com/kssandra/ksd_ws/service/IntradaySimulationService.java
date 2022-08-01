package com.kssandra.ksd_ws.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kssandra.ksd_common.dto.CryptoCurrencyDto;
import com.kssandra.ksd_common.dto.CryptoDataDto;
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

@Service
public class IntradaySimulationService {

	@Autowired
	CryptoCurrencyDao cxCurrDao;

	@Autowired
	PredictionDao predictionDao;

	@Autowired
	CryptoDataDao cxDataDao;

	@Autowired
	PredictionSuccessDao predictionSuccessDao;

	public IntradaySimulationResponse getSimulation(IntradaySimulationRequest intraRq) throws KsdServiceException {

		IntradaySimulationResponse response = null;

		CryptoCurrencyDto cxCurrDto = cxCurrDao.findByCode(intraRq.getCxCurr().getValue());
		boolean hasDate = StringUtils.isNotBlank(intraRq.getDateTime());
		if (cxCurrDto != null) {

			List<PredictionDto> predictions = null;

			if (hasDate) {
				LocalDateTime rqDate = LocalDateTime.parse(intraRq.getDateTime(),
						DateTimeFormatter.ofPattern(DateUtils.FORMAT_YYYYMMDD_HHMM));
				predictions = predictionDao.findByDate(cxCurrDto, rqDate);
			} else {
				predictions = predictionDao.findAfterDate(cxCurrDto, LocalDateTime.now(),
						LocalDateTime.now().plusDays(1));
			}

			IntervalEnum interval = IntervalEnum.fromName(intraRq.getInterval());

			response = new IntradaySimulationResponse();
			response.setCxCurr(intraRq.getCxCurr().getValue());
			response.setExCurr(intraRq.getExCurr().getValue());
			Double currVal = getCurrVal(cxCurrDto);
			response.setItems(getItems(predictions, interval, intraRq.getAmount(), intraRq.getPurchaseFee(),
					intraRq.getSaleFee(), intraRq.getDateTime(), currVal));

		} else {
			throw new KsdServiceException(
					"Any cxcurrency found in DB for code: ".concat(intraRq.getCxCurr().getValue()));
		}

		return response;
	}

	private List<IntradaySimulationResponseItem> getItems(List<PredictionDto> predictions, IntervalEnum interval,
			Double amount, Double purchaseFee, Double saleFee, String dateTime, Double currVal) {
		List<IntradaySimulationResponseItem> items = new ArrayList<>();

		if (!predictions.isEmpty()) {

			List<PredictionSuccessDto> predSuccess = predictionSuccessDao
					.findSuccess(predictions.get(0).getCxCurrencyDto());

			Map<LocalDateTime, PredictionDto> bestPredictions = IntradayPredictionService
					.getBestPredictions(predictions, predSuccess, interval);

			bestPredictions.values().stream().sorted((e1, e2) -> e1.getPredictTime().compareTo(e2.getPredictTime()))
					.forEach(pred -> items.add(predToItem(pred, amount, purchaseFee, saleFee, dateTime, currVal)));
		}

		return items;
	}

	private IntradaySimulationResponseItem predToItem(PredictionDto dto, Double amount, Double purchaseFee,
			Double saleFee, String dateTime, Double currVal) {
		IntradaySimulationResponseItem item = new IntradaySimulationResponseItem();

		item.setDateTime(dateTime != null ? dateTime
				: dto.getPredictTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
		item.setExpectedVal(dto.getPredictVal());

		item.setSuccess(IntradayPredictionService.beautifySuccess(dto.getSuccess()));
		item.setExpectedVal(PriceUtils.roundPrice(amount / currVal * dto.getPredictVal()));

		saleFee = (saleFee == null || saleFee == 0) ? 1 : saleFee;
		purchaseFee = (purchaseFee == null || purchaseFee == 0) ? 1 : purchaseFee;
		item.setProfit(PriceUtils.roundPrice((item.getExpectedVal() * saleFee) - (amount * purchaseFee)));

		return item;
	}

	private Double getCurrVal(CryptoCurrencyDto cxCurrDto) {
		CryptoDataDto cxDataDto = cxDataDao.getLastInserted(cxCurrDto);
		return (cxDataDto.getHigh() + cxDataDto.getLow()) / 2;
	}
}
