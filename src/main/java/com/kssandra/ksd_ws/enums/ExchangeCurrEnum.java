package com.kssandra.ksd_ws.enums;

public enum ExchangeCurrEnum {

	EUR("EUR");

	private String value;

	ExchangeCurrEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
