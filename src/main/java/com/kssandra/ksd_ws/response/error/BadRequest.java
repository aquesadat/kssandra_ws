package com.kssandra.ksd_ws.response.error;

import java.util.ArrayList;
import java.util.List;

public class BadRequest {

	private List<String> errors;

	public BadRequest addErrorsItem(String errorsItem) {

		if (this.errors == null) {
			this.errors = new ArrayList<>();
		}
		this.errors.add(errorsItem);
		return this;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

}
