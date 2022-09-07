package com.kssandra.ksd_ws.validation;

import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface Validations {

	@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@Constraint(validatedBy = ValueOfEnumValidator.class)
	public @interface ValueOfEnumConstraint {
		Class<? extends Enum<?>> enumClass();

		String message() default "Invalid field value";

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};
	}

	@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@Constraint(validatedBy = DateFormatValidator.class)
	public @interface DateFormatConstraint {
		String message() default "Invalid date format";

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};
	}

}
