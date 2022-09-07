package com.kssandra.ksd_ws.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntradaySimulationResponseItem implements Serializable {

	private static final long serialVersionUID = -4412293092637395118L;

	private String dateTime;

	private Double expectedVal;

	private String success;

	private Double profit;

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

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public Double getProfit() {
		return profit;
	}

	public void setProfit(Double profit) {
		this.profit = profit;
	}

}
