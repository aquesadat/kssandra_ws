package com.kssandra.ksd_ws.response;

import java.util.List;

public class IntradaySimulationResponse {

	private String cxCurr;

	private String exCurr;

	private List<IntradaySimulationResponseItem> items;

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

	public List<IntradaySimulationResponseItem> getItems() {
		return items;
	}

	public void setItems(List<IntradaySimulationResponseItem> items) {
		this.items = items;
	}

}
