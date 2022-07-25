package com.kssandra.ksd_ws.request;

import java.io.Serializable;

import com.kssandra.ksd_ws.enums.CryptoCurrEnum;
import com.kssandra.ksd_ws.enums.ExchangeCurrEnum;

public class IntraDayRequest implements Serializable {

	private static final long serialVersionUID = -2112674008815216328L;

	private CryptoCurrEnum cxCurr;

	private ExchangeCurrEnum exCurr;

	private String interval;

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

}
