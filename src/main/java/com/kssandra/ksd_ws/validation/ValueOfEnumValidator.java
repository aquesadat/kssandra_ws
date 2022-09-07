package com.kssandra.ksd_ws.validation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.kssandra.ksd_ws.validation.Validations.ValueOfEnumConstraint;

public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnumConstraint, CharSequence> {
	private List<String> acceptedValues;

	@Override
	public void initialize(ValueOfEnumConstraint annotation) {
		acceptedValues = Stream.of(annotation.enumClass().getEnumConstants()).map(Enum::name)
				.collect(Collectors.toList());
	}

	@Override
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}

		return acceptedValues.contains(value.toString());
	}
}
