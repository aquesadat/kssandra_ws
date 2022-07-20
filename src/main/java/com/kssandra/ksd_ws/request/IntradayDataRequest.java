package com.kssandra.ksd_ws.request;

import java.io.Serializable;

import com.kssandra.ksd_ws.enums.CryptoCurrEnum;
import com.kssandra.ksd_ws.enums.ExchangeCurrEnum;

public class IntradayDataRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5719470876950246960L;

	private CryptoCurrEnum cxCurr;

	private ExchangeCurrEnum exCurr;

	private String interval;

	private boolean extended;

	public CryptoCurrEnum getCxCurr() {
		return cxCurr;
	}

	public void setCxCurr(CryptoCurrEnum cxCurr) {
		this.cxCurr = cxCurr;
	}

	public ExchangeCurrEnum getExCurr() {
		return exCurr;
	}

	public void setExCurr(ExchangeCurrEnum exCurr) {
		this.exCurr = exCurr;
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
