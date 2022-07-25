package com.kssandra.ksd_ws.validation;

import org.apache.commons.lang3.StringUtils;

import com.kssandra.ksd_ws.enums.IntervalEnum;
import com.kssandra.ksd_ws.request.IntradayPredictionRequest;
import com.kssandra.ksd_ws.response.error.BadRequest;

public class IntradayPredictionValidator {

	public static BadRequest validate(IntradayPredictionRequest intraRq) {

		BadRequest badRq = new BadRequest();

		if (intraRq != null) {
			if (intraRq.getCxCurr() == null) {
				badRq.addErrorsItem(ValErrors.MISSING_INVALID.concat("cxCurr"));
			}

			if (intraRq.getExCurr() == null) {
				badRq.addErrorsItem(ValErrors.MISSING_INVALID.concat("exCurr"));
			}

			if (StringUtils.isBlank(intraRq.getInterval()) || IntervalEnum.fromName(intraRq.getInterval()) == null) {
				badRq.addErrorsItem(ValErrors.MISSING_INVALID.concat("interval"));
			}

		} else {
			badRq.addErrorsItem(ValErrors.MISSING_RQ_BODY);
		}

		return badRq;
	}

}
