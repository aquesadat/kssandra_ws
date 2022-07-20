package com.kssandra.ksd_ws.enums;

public enum CryptoCurrEnum {

	ADA("ADA"), BTC("BTC"), DOT("DOT"), LTC("LTC"), UNI("UNI"), BNB("BNB"), DOGE("DOGE"), ETH("ETH"), SOL("SOL"),
	XRP("XRP");

	private String value;

	CryptoCurrEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
