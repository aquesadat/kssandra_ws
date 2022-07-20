package com.kssandra.ksd_ws.response;

import java.util.List;

public class IntradayDataResponse {

	private String cxCurr;

	private String exCurr;

	private List<IntradayDataResponseItem> items;

	public List<IntradayDataResponseItem> getItems() {
		return items;
	}

	public void setItems(List<IntradayDataResponseItem> items) {
		this.items = items;
	}

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

}
