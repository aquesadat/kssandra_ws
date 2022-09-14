package com.kssandra.ksd_ws.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Response item for /intraday/data endpoint.
 * 
 * @author aquesada
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntradayDataResponseItem implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6130028840719648283L;

	/** The date time. */
	private String dateTime;

	/** The open price (for specified interval). */
	private Double open;

	/** The close price (for specified interval). */
	private Double close;

	/** The high price (for specified interval). */
	private Double high;

	/** The low price (for specified interval). */
	private Double low;

	/** The average price (for specified interval). */
	private Double avg;

	/**
	 * Gets the date time.
	 *
	 * @return the date time
	 */
	public String getDateTime() {
		return dateTime;
	}

	/**
	 * Sets the date time.
	 *
	 * @param dateTime the new date time
	 */
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	/**
	 * Gets the open.
	 *
	 * @return the open
	 */
	public Double getOpen() {
		return open;
	}

	/**
	 * Sets the open.
	 *
	 * @param open the new open
	 */
	public void setOpen(Double open) {
		this.open = open;
	}

	/**
	 * Gets the close.
	 *
	 * @return the close
	 */
	public Double getClose() {
		return close;
	}

	/**
	 * Sets the close.
	 *
	 * @param close the new close
	 */
	public void setClose(Double close) {
		this.close = close;
	}

	/**
	 * Gets the high.
	 *
	 * @return the high
	 */
	public Double getHigh() {
		return high;
	}

	/**
	 * Sets the high.
	 *
	 * @param high the new high
	 */
	public void setHigh(Double high) {
		this.high = high;
	}

	/**
	 * Gets the low.
	 *
	 * @return the low
	 */
	public Double getLow() {
		return low;
	}

	/**
	 * Sets the low.
	 *
	 * @param low the new low
	 */
	public void setLow(Double low) {
		this.low = low;
	}

	/**
	 * Gets the avg.
	 *
	 * @return the avg
	 */
	public Double getAvg() {
		return avg;
	}

	/**
	 * Sets the avg.
	 *
	 * @param avg the new avg
	 */
	public void setAvg(Double avg) {
		this.avg = avg;
	}

}
