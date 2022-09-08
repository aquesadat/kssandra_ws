package com.kssandra.ksd_ws.request;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.kssandra.ksd_ws.enums.CryptoCurrEnum;
import com.kssandra.ksd_ws.enums.ExchangeCurrEnum;
import com.kssandra.ksd_ws.enums.IntervalEnum;
import com.kssandra.ksd_ws.validation.ValErrors;
import com.kssandra.ksd_ws.validation.Validations.ValueOfEnumConstraint;

public class IntradayPredictionRequest implements Serializable {

	private static final long serialVersionUID = -6748910394905639857L;

	@NotNull(message = ValErrors.MISSING)
	@ValueOfEnumConstraint(enumClass = CryptoCurrEnum.class)
	private String cxCurr;

	@NotNull(message = ValErrors.MISSING)
	@ValueOfEnumConstraint(enumClass = ExchangeCurrEnum.class)
	private String exCurr;

	@NotNull(message = ValErrors.MISSING)
	@ValueOfEnumConstraint(enumClass = IntervalEnum.class)
	private String interval;

	public String getCxCurr() {
		return cxCurr;
	}

	public void setCxCurr(String cxCurr) {
		this.cxCurr = cxCurr;
	}

	public String getExCurr() {
		return exCurr;
	}

	public void setExCurr(String exCurr) {
		this.exCurr = exCurr;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

}
