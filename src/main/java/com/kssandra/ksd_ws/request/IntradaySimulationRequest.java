package com.kssandra.ksd_ws.request;

public class IntradaySimulationRequest extends IntraDayRequest {

	private static final long serialVersionUID = -5644775770505189525L;

	private Double amount;

	private Double purchaseFee;

	private Double saleFee;

	private String dateTime;

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getPurchaseFee() {
		return purchaseFee;
	}

	public void setPurchaseFee(Double purchaseFee) {
		this.purchaseFee = purchaseFee;
	}

	public Double getSaleFee() {
		return saleFee;
	}

	public void setSaleFee(Double saleFee) {
		this.saleFee = saleFee;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

}
