package com.kssandra.ksd_ws.request;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import com.kssandra.ksd_ws.enums.ExchangeCurrEnum;
import com.kssandra.ksd_ws.service.IntradaySuggestionService;
import com.kssandra.ksd_ws.validation.ValErrors;
import com.kssandra.ksd_ws.validation.Validations.ValueOfEnumConstraint;

public class IntradaySuggestionRequest implements Serializable {

	private static final long serialVersionUID = 454302144941537932L;

	@NotNull(message = ValErrors.MISSING)
	@ValueOfEnumConstraint(enumClass = ExchangeCurrEnum.class)
	private String exCurr;

	@Range(min = 1, max = IntradaySuggestionService.MAX_RESULTS, message = ValErrors.INVALID)
	private Integer numResult;

	public String getExCurr() {
		return exCurr;
	}

	public void setExCurr(String exCurr) {
		this.exCurr = exCurr;
	}

	public Integer getNumResult() {
		return numResult;
	}

	public void setNumResult(Integer numResult) {
		this.numResult = numResult;
	}

}
