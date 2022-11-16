package com.kssandra.ksd_ws.response;

import java.io.Serializable;

public class IntradaySuggestionResponseItem implements Serializable {

	private static final long serialVersionUID = -4777776692011707410L;

	private int rank;

	private String cxCurr;

	private String cxCurrDesc;

	private Double currVal;

	private Double expectedVal;

	private String expectedRaise;

	private String success;

	public String getCxCurr() {
		return cxCurr;
	}

	public void setCxCurr(String cxCurr) {
		this.cxCurr = cxCurr;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public Double getExpectedVal() {
		return expectedVal;
	}

	public void setExpectedVal(Double expectedVal) {
		this.expectedVal = expectedVal;
	}

	public String getExpectedRaise() {
		return expectedRaise;
	}

	public void setExpectedRaise(String expectedRaise) {
		this.expectedRaise = expectedRaise;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getCxCurrDesc() {
		return cxCurrDesc;
	}

	public void setCxCurrDesc(String cxCurrDesc) {
		this.cxCurrDesc = cxCurrDesc;
	}

	public Double getCurrVal() {
		return currVal;
	}

	public void setCurrVal(Double currVal) {
		this.currVal = currVal;
	}

}
