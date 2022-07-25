package com.kssandra.ksd_ws.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntradaySimulationResponseItem {

	private String dateTime;

	private Double expectedVal;
	
	private Double success;

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public Double getExpectedVal() {
		return expectedVal;
	}

	public void setExpectedVal(Double expectedVal) {
		this.expectedVal = expectedVal;
	}

	public Double getSuccess() {
		return success;
	}

	public void setSuccess(Double success) {
		this.success = success;
	}

}
