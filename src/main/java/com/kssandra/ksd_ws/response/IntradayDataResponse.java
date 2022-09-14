package com.kssandra.ksd_ws.response;

import java.io.Serializable;
import java.util.List;

/**
 * Response for /intraday/data endpoint.
 * 
 * @author aquesada
 */
public class IntradayDataResponse implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4213939737226741515L;

	/** Crypto-currency code. */
	private String cxCurr;

	/** Currency code to express the price. */
	private String exCurr;

	/** Response items */
	private List<IntradayDataResponseItem> items;

	/**
	 * Gets the items.
	 *
	 * @return the items
	 */
	public List<IntradayDataResponseItem> getItems() {
		return items;
	}

	/**
	 * Sets the items.
	 *
	 * @param items the new items
	 */
	public void setItems(List<IntradayDataResponseItem> items) {
		this.items = items;
	}

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

}
