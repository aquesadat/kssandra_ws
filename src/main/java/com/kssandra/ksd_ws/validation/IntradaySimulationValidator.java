package com.kssandra.ksd_ws.validation;

import org.apache.commons.lang3.StringUtils;

import com.kssandra.ksd_common.util.DateUtils;
import com.kssandra.ksd_ws.enums.IntervalEnum;
import com.kssandra.ksd_ws.request.IntradaySimulationRequest;
import com.kssandra.ksd_ws.response.error.BadRequest;

public class IntradaySimulationValidator {

	public static BadRequest validate(IntradaySimulationRequest intraRq) {

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

			if (intraRq.getAmount() == null || intraRq.getAmount() <= 0) {
				badRq.addErrorsItem(ValErrors.MISSING_INVALID.concat("amount"));
			}

			if (intraRq.getPurchaseCommision() != null && intraRq.getPurchaseCommision() < 0) {
				badRq.addErrorsItem(ValErrors.INVALID.concat("purchaseCommision"));
			}

			if (intraRq.getSaleCommision() != null && intraRq.getSaleCommision() < 0) {
				badRq.addErrorsItem(ValErrors.INVALID.concat("saleCommision"));
			}

			if (StringUtils.isNotBlank(intraRq.getDateTime())) {
				ValidationUtils.validDateTime(intraRq.getDateTime(), DateUtils.FORMAT_YYYYMMDD_HHMM);
			}

		} else {
			badRq.addErrorsItem(ValErrors.MISSING_RQ_BODY);
		}

		return badRq;
	}

}
