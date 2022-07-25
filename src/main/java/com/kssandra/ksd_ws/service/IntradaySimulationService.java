package com.kssandra.ksd_ws.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kssandra.ksd_common.dto.CryptoCurrencyDto;
import com.kssandra.ksd_common.dto.CryptoDataDto;
import com.kssandra.ksd_common.dto.PredictionDto;
import com.kssandra.ksd_common.dto.PredictionSuccessDto;
import com.kssandra.ksd_common.util.PriceUtils;
import com.kssandra.ksd_persistence.dao.CryptoCurrencyDao;
import com.kssandra.ksd_persistence.dao.CryptoDataDao;
import com.kssandra.ksd_persistence.dao.PredictionDao;
import com.kssandra.ksd_persistence.dao.PredictionSuccessDao;
import com.kssandra.ksd_ws.enums.IntervalEnum;
import com.kssandra.ksd_ws.exception.KsdServiceException;
import com.kssandra.ksd_ws.request.IntradayDataRequest;
import com.kssandra.ksd_ws.request.IntradaySimulationRequest;
import com.kssandra.ksd_ws.response.IntradayDataResponse;
import com.kssandra.ksd_ws.response.IntradayDataResponseItem;
import com.kssandra.ksd_ws.response.IntradayPredictionResponse;
import com.kssandra.ksd_ws.response.IntradayPredictionResponseItem;
import com.kssandra.ksd_ws.response.IntradaySimulationResponse;
import com.kssandra.ksd_ws.response.IntradaySimulationResponseItem;

@Service
public class IntradaySimulationService {

	@Autowired
	CryptoCurrencyDao cxCurrDao;

	@Autowired
	PredictionDao predictionDao;

	@Autowired
	PredictionSuccessDao predictionSuccessDao;

	public IntradaySimulationResponse getSimulation(IntradaySimulationRequest intraRq) throws KsdServiceException {

		IntradaySimulationResponse response = null;

		CryptoCurrencyDto cxCurrDto = cxCurrDao.findByCode(intraRq.getCxCurr().getValue());

		if (cxCurrDto != null) {
			List<PredictionDto> data = predictionDao.findAfterDate(cxCurrDto, LocalDateTime.now(),
					LocalDateTime.now().plusDays(1));
			IntervalEnum interval = IntervalEnum.fromName(intraRq.getInterval());

			response = new IntradaySimulationResponse();
			response.setCxCurr(intraRq.getCxCurr().getValue());
			response.setExCurr(intraRq.getExCurr().getValue());
			response.setItems(getItems(data, interval));

		} else {
			throw new KsdServiceException(
					"Any cxcurrency found in DB for code: ".concat(intraRq.getCxCurr().getValue()));
		}

		return response;
	}

	private List<IntradaySimulationResponseItem> getItems(List<PredictionDto> predictions, IntervalEnum interval) {
		List<IntradaySimulationResponseItem> items = new ArrayList<>();

		predictions.stream().filter(dto -> interval.getValues().contains(dto.getPredictTime().getMinute()))
				.map(dto -> items.add(getIntraRsItem(dto))).collect(Collectors.toList());

		return items;
	}

	private IntradaySimulationResponseItem getIntraRsItem(PredictionDto dto) {
		IntradaySimulationResponseItem item = new IntradaySimulationResponseItem();

		item.setExpectedVal(dto.getPredictVal());
		List<PredictionSuccessDto> predSuccess = predictionSuccessDao.findSuccess(dto.getCxCurrencyDto(),
				dto.getSampleSize(), dto.getAdvance());

		predSuccess.stream().max((ps1, ps2) -> ps1.getSuccess().compareTo(ps2.getSuccess()))
				.ifPresent(elem -> item.setSuccess(elem.getSuccess()));

		item.setDateTime(dto.getPredictTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

		return item;
	}

}
