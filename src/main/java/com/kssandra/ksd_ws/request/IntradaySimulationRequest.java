package com.kssandra.ksd_ws.request;

import java.io.Serializable;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import com.kssandra.ksd_ws.enums.CryptoCurrEnum;
import com.kssandra.ksd_ws.enums.ExchangeCurrEnum;
import com.kssandra.ksd_ws.enums.IntervalEnum;
import com.kssandra.ksd_ws.validation.ValErrors;
import com.kssandra.ksd_ws.validation.Validations.DateFormatConstraint;
import com.kssandra.ksd_ws.validation.Validations.ValueOfEnumConstraint;

public class IntradaySimulationRequest implements Serializable {

	private static final long serialVersionUID = -5888944598194712002L;

	@NotNull(message = ValErrors.MISSING)
	@ValueOfEnumConstraint(enumClass = CryptoCurrEnum.class)
	private String cxCurr;

	@NotNull(message = ValErrors.MISSING)
	@ValueOfEnumConstraint(enumClass = ExchangeCurrEnum.class)
	private String exCurr;

	@NotNull(message = ValErrors.MISSING)
	@ValueOfEnumConstraint(enumClass = IntervalEnum.class)
	private String interval;

	@NotNull(message = ValErrors.MISSING)
	@DecimalMin(value = "0", inclusive = false, message = ValErrors.INVALID)
	private Double amount;

	@Range(min = 0, max = 100, message = ValErrors.INVALID)
	private Double purchaseFee;

	@Range(min = 0, max = 100, message = ValErrors.INVALID)
	private Double saleFee;

	@DateFormatConstraint
	private String dateTime;

	public String getCxCurr() {
		return cxCurr;
	}

	public String getExCurr() {
		return exCurr;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

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
