package com.kssandra.ksd_ws.response;

import java.io.Serializable;
import java.util.List;

public class IntradaySuggestionResponse implements Serializable {

	private static final long serialVersionUID = 1887125390525890113L;

	private String exCurr;

	private List<IntradaySuggestionResponseItem> items;

	public String getExCurr() {
		return exCurr;
	}

	public void setExCurr(String exCurr) {
		this.exCurr = exCurr;
	}

	public List<IntradaySuggestionResponseItem> getItems() {
		return items;
	}

	public void setItems(List<IntradaySuggestionResponseItem> items) {
		this.items = items;
	}

}
