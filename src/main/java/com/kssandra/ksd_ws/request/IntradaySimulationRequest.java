package com.kssandra.ksd_ws.request;

public class IntradaySimulationRequest extends IntraDayRequest {

	private static final long serialVersionUID = -5644775770505189525L;

	private Double amount;

	private Double purchaseCommision;

	private Double saleCommision;

	private String dateTime;

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getPurchaseCommision() {
		return purchaseCommision;
	}

	public void setPurchaseCommision(Double purchaseCommision) {
		this.purchaseCommision = purchaseCommision;
	}

	public Double getSaleCommision() {
		return saleCommision;
	}

	public void setSaleCommision(Double saleCommision) {
		this.saleCommision = saleCommision;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

}
