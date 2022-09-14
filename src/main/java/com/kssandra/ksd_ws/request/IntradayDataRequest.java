package com.kssandra.ksd_ws.request;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.kssandra.ksd_ws.enums.CryptoCurrEnum;
import com.kssandra.ksd_ws.enums.ExchangeCurrEnum;
import com.kssandra.ksd_ws.enums.IntervalEnum;
import com.kssandra.ksd_ws.validation.ValErrors;
import com.kssandra.ksd_ws.validation.Validations.ValueOfEnumConstraint;

/**
 * Request for /intraday/data endpoint.
 *
 * @author aquesada
 */
public class IntradayDataRequest implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8284543048239832223L;

	/** Crypto-currency code. */
	@NotNull(message = ValErrors.MISSING)
	@ValueOfEnumConstraint(enumClass = CryptoCurrEnum.class)
	private String cxCurr;

	/** Currency code to express the price. */
	@NotNull(message = ValErrors.MISSING)
	@ValueOfEnumConstraint(enumClass = ExchangeCurrEnum.class)
	private String exCurr;

	/**
	 * Time interval to return price data (15 mins, 60 mins...)
	 */
	@NotNull(message = ValErrors.MISSING)
	@ValueOfEnumConstraint(enumClass = IntervalEnum.class)
	private String interval;

	/**
	 * Show reduced data price (high and low) or extendend (open, close, high and
	 * low).
	 */
	private boolean extended;

	/**
	 * Gets the cx curr.
	 *
	 * @return the cx curr
	 */
	public String getCxCurr() {
		return cxCurr;
	}

	/**
	 * Sets the cx curr.
	 *
	 * @param cxCurr the new cx curr
	 */
	public void setCxCurr(String cxCurr) {
		this.cxCurr = cxCurr;
	}

	/**
	 * Gets the ex curr.
	 *
	 * @return the ex curr
	 */
	public String getExCurr() {
		return exCurr;
	}

	/**
	 * Sets the ex curr.
	 *
	 * @param exCurr the new ex curr
	 */
	public void setExCurr(String exCurr) {
		this.exCurr = exCurr;
	}

	/**
	 * Gets the interval.
	 *
	 * @return the interval
	 */
	public String getInterval() {
		return interval;
	}

	/**
	 * Sets the interval.
	 *
	 * @param interval the new interval
	 */
	public void setInterval(String interval) {
		this.interval = interval;
	}

	/**
	 * Checks if is extended.
	 *
	 * @return true, if is extended
	 */
	public boolean isExtended() {
		return extended;
	}

	/**
	 * Sets the extended.
	 *
	 * @param extended the new extended
	 */
	public void setExtended(boolean extended) {
		this.extended = extended;
	}

}
