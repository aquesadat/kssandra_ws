package com.kssandra.ksd_ws.validation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

import com.kssandra.ksd_common.util.DateUtils;
import com.kssandra.ksd_ws.enums.IntervalEnum;
import com.kssandra.ksd_ws.request.IntradaySimulationRequest;
import com.kssandra.ksd_ws.response.error.BadRequest;

public class IntradaySimulationValidator {

	public static BadRequest validate(IntradaySimulationRequest intraRq) {

		BadRequest badRq = new BadRequest();

		if (intraRq != null) {

			boolean hasDate = false;
			if (intraRq.getCxCurr() == null) {
				badRq.addErrorsItem(ValErrors.MISSING_INVALID.concat("cxCurr"));
			}

			if (intraRq.getExCurr() == null) {
				badRq.addErrorsItem(ValErrors.MISSING_INVALID.concat("exCurr"));
			}

			if (StringUtils.isNotBlank(intraRq.getDateTime())) {
				if (!ValidationUtils.validDateTime(intraRq.getDateTime(), DateUtils.FORMAT_YYYYMMDD_HHMM)) {
					badRq.addErrorsItem(ValErrors.INVALID.concat("dateTime"));
				} else {
					LocalDateTime rqDate = LocalDateTime.parse(intraRq.getDateTime(),
							DateTimeFormatter.ofPattern(DateUtils.FORMAT_YYYYMMDD_HHMM));
					if (rqDate.isAfter(LocalDateTime.now().plusDays(1))) {
						badRq.addErrorsItem(
								ValErrors.INVALID.concat("dateTime").concat(". It must be in the next 24h"));
					} else {
						hasDate = true;
					}
				}
			}

			if (hasDate && StringUtils.isBlank(intraRq.getInterval())
					&& IntervalEnum.fromName(intraRq.getInterval()) == null) {
				badRq.addErrorsItem(ValErrors.MISSING_INVALID.concat("interval"));
			}

			if (intraRq.getAmount() == null || intraRq.getAmount() <= 0) {
				badRq.addErrorsItem(ValErrors.MISSING_INVALID.concat("amount"));
			}

			if (intraRq.getPurchaseFee() != null && intraRq.getPurchaseFee() < 0) {
				badRq.addErrorsItem(ValErrors.INVALID.concat("purchaseFee"));
			}

			if (intraRq.getSaleFee() != null && intraRq.getSaleFee() < 0) {
				badRq.addErrorsItem(ValErrors.INVALID.concat("saleFee"));
			}

		} else {
			badRq.addErrorsItem(ValErrors.MISSING_RQ_BODY);
		}

		return badRq;
	}

}
