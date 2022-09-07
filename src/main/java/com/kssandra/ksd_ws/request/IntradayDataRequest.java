package com.kssandra.ksd_ws.request;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.kssandra.ksd_ws.enums.CryptoCurrEnum;
import com.kssandra.ksd_ws.enums.ExchangeCurrEnum;
import com.kssandra.ksd_ws.enums.IntervalEnum;
import com.kssandra.ksd_ws.validation.ValErrors;
import com.kssandra.ksd_ws.validation.Validations.ValueOfEnumConstraint;

public class IntradayDataRequest implements Serializable {

	private static final long serialVersionUID = -8284543048239832223L;

	@NotNull(message = ValErrors.MISSING)
	@ValueOfEnumConstraint(enumClass = CryptoCurrEnum.class)
	private String cxCurr;

	@NotNull(message = ValErrors.MISSING)
	@ValueOfEnumConstraint(enumClass = ExchangeCurrEnum.class)
	private String exCurr;

	@NotNull(message = ValErrors.MISSING)
	@ValueOfEnumConstraint(enumClass = IntervalEnum.class)
	private String interval;

	private boolean extended;

	public String getCxCurr() {
		return cxCurr;
	}

	public String getExCurr() {
		return exCurr;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public boolean isExtended() {
		return extended;
	}

	public void setExtended(boolean extended) {
		this.extended = extended;
	}

}
