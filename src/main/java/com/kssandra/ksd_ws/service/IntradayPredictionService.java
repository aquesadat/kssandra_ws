package com.kssandra.ksd_ws.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kssandra.ksd_common.dto.CryptoCurrencyDto;
import com.kssandra.ksd_common.dto.PredictionDto;
import com.kssandra.ksd_common.dto.PredictionSuccessDto;
import com.kssandra.ksd_persistence.dao.CryptoCurrencyDao;
import com.kssandra.ksd_persistence.dao.PredictionDao;
import com.kssandra.ksd_persistence.dao.PredictionSuccessDao;
import com.kssandra.ksd_ws.enums.IntervalEnum;
import com.kssandra.ksd_ws.exception.KsdServiceException;
import com.kssandra.ksd_ws.request.IntradayPredictionRequest;
import com.kssandra.ksd_ws.response.IntradayPredictionResponse;
import com.kssandra.ksd_ws.response.IntradayPredictionResponseItem;

@Service
public class IntradayPredictionService {

	@Autowired
	PredictionDao predictionDao;

	@Autowired
	PredictionSuccessDao predictionSuccessDao;

	@Autowired
	CryptoCurrencyDao cxCurrDao;

	public IntradayPredictionResponse getPrediction(IntradayPredictionRequest intraRq) throws KsdServiceException {
		IntradayPredictionResponse response = null;

		CryptoCurrencyDto cxCurrDto = cxCurrDao.findByCode(intraRq.getCxCurr().getValue());

		if (cxCurrDto != null) {
			List<PredictionDto> data = predictionDao.findAfterDate(cxCurrDto, LocalDateTime.now(),
					LocalDateTime.now().plusDays(1));
			IntervalEnum interval = IntervalEnum.fromName(intraRq.getInterval());

			response = new IntradayPredictionResponse();
			response.setCxCurr(intraRq.getCxCurr().getValue());
			response.setExCurr(intraRq.getExCurr().getValue());
			response.setItems(getItems(data, interval));

		} else {
			throw new KsdServiceException(
					"Any cxcurrency found in DB for code: ".concat(intraRq.getCxCurr().getValue()));
		}

		return response;
	}

	/**
	 * 1.- Recupera las combinaciones de sample-advance con el success
	 * correspondiente 2.- Para cada prediction reucperada de bbdd, se queda con
	 * aquella que tiene mejor success en prediciones pasada.
	 * 
	 * @param predictions
	 * @param interval
	 * @return
	 */
	private List<IntradayPredictionResponseItem> getItems(List<PredictionDto> predictions, IntervalEnum interval) {
		List<IntradayPredictionResponseItem> items = new ArrayList<>();

		if (!predictions.isEmpty()) {

			List<PredictionSuccessDto> predSuccess = predictionSuccessDao
					.findSuccess(predictions.get(0).getCxCurrencyDto());

			Map<LocalDateTime, IntradayPredictionResponseItem> bestPredictions = new LinkedHashMap<>();

			predictions.stream().filter(dto -> interval.getValues().contains(dto.getPredictTime().getMinute()))
					.forEach(dto -> {
						if (bestPredictions.containsKey(dto.getPredictTime())) {
							Double success = getSuccess(dto, predSuccess);
							if (success != null && bestPredictions.get(dto.getPredictTime()).getSuccess() > success) {
								bestPredictions.get(dto.getPredictTime()).setExpectedVal(dto.getPredictVal());
								bestPredictions.get(dto.getPredictTime()).setSuccess(success);
							}
						} else {
							bestPredictions.put(dto.getPredictTime(), getIntraRsItem(dto, predSuccess));
						}
					});

			items = bestPredictions.values().stream().collect(Collectors.toList());

		}

		return items;
	}

	private IntradayPredictionResponseItem getIntraRsItem(PredictionDto dto, List<PredictionSuccessDto> predSuccess) {
		IntradayPredictionResponseItem item = new IntradayPredictionResponseItem();

		item.setExpectedVal(dto.getPredictVal());
		item.setSuccess(getSuccess(dto, predSuccess));
		item.setDateTime(dto.getPredictTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

		return item;
	}

	private Double getSuccess(PredictionDto predictionDto, List<PredictionSuccessDto> predSuccess) {

		Optional<PredictionSuccessDto> result = predSuccess.stream()
				.filter(dto -> dto.getAdvance() == predictionDto.getAdvance()
						&& dto.getSampleSize() == predictionDto.getSampleSize())
				.findFirst();

		return result.isPresent() ? result.get().getSuccess() : null;
	}

}
