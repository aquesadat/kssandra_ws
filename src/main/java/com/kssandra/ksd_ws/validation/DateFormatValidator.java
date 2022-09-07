package com.kssandra.ksd_ws.validation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.kssandra.ksd_common.util.DateUtils;
import com.kssandra.ksd_ws.validation.Validations.DateFormatConstraint;

public class DateFormatValidator implements ConstraintValidator<DateFormatConstraint, String> {

	@Override
	public void initialize(DateFormatConstraint dateTime) {
	}

	@Override
	public boolean isValid(String dateTime, ConstraintValidatorContext cxt) {
		if (dateTime != null) {
			try {
				DateTimeFormatter DATE_WITH_TIME_FORMAT = DateTimeFormatter.ofPattern(DateUtils.FORMAT_DDMMYYYY_HHMM);
				LocalDateTime.parse(dateTime, DATE_WITH_TIME_FORMAT);
			} catch (Exception ex) {
				return false;
			}
		}
		return true;
	}

}
