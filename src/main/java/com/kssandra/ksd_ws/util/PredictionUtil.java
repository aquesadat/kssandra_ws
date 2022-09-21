package com.kssandra.ksd_ws.util;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.kssandra.ksd_common.dto.PredictionDto;
import com.kssandra.ksd_common.dto.PredictionSuccessDto;
import com.kssandra.ksd_ws.enums.IntervalEnum;

public class PredictionUtil {

	/**
	 * Gets the best future prediction (sample-advance combination) for every time
	 * in the next 24h according to past evaluated predictions
	 * 
	 * @param predictions the future predictions
	 * @param predSuccess the past evaluated predictions
	 * @param interval    the interval
	 * @return time-best prediction map
	 */
	public static Map<LocalDateTime, PredictionDto> getBestPredictions(List<PredictionDto> predictions,
			List<PredictionSuccessDto> predSuccess, IntervalEnum interval) {

		Map<LocalDateTime, PredictionDto> bestPredictions = new LinkedHashMap<>();

		predictions.stream().filter(
				// Remove predictions with predict time minutes not match interval values
				interval == null ? dto -> true : dto -> interval.getValues().contains(dto.getPredictTime().getMinute()))
				.forEach(dto -> {
					if (bestPredictions.containsKey(dto.getPredictTime())) { // Prediction already exists in the map
						Double success = getSuccess(dto, predSuccess);

						// Updates the stored prediction if the new success is higher than the current
						// one
						if (success != null && (bestPredictions.get(dto.getPredictTime()).getSuccess() == null
								|| (bestPredictions.get(dto.getPredictTime()).getSuccess() != null
										&& bestPredictions.get(dto.getPredictTime()).getSuccess() < success))) {
							bestPredictions.get(dto.getPredictTime()).setPredictVal(dto.getPredictVal());
							bestPredictions.get(dto.getPredictTime()).setSuccess(success);
						}
					} else { // If prediction isn't yet in the result map
						// Set success to prediction according to identical past evaluated predictions
						dto.setSuccess(getSuccess(dto, predSuccess));
						bestPredictions.put(dto.getPredictTime(), dto);
					}
				});

		return bestPredictions;
	}

	/**
	 * Get success value for a given prediction
	 *
	 * @param predictionDto the future prediction
	 * @param predSuccess   the past evaluated predictions
	 * @return the success value
	 */
	public static Double getSuccess(PredictionDto predictionDto, List<PredictionSuccessDto> predSuccess) {

		// For one future prediction, finds into past evaluated predictions list one
		// with the same advance an sample size value
		Optional<PredictionSuccessDto> result = predSuccess.stream()
				.filter(dto -> dto.getAdvance() == predictionDto.getAdvance()
						&& dto.getSampleSize() == predictionDto.getSampleSize())
				.findFirst();

		return result.isPresent() ? result.get().getSuccess() : null;
	}

	/**
	 * Beautify the success value extracted from DB to use it in the WS responses
	 *
	 * @param success the success value
	 * @return the beautified success as percent
	 */
	public static String beautifySuccess(Double success) {

		String result;

		if (success == null) {
			result = "N/A";
		} else {
			double aux = 100 - (Math.abs(success) * 20);
			if (aux > 90) {
				result = ">90%";
			} else if (aux < 40) {
				result = "<40%";
			} else {
				result = String.valueOf((int) aux) + "%";
			}
		}

		return result;
	}

}
