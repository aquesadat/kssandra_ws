package com.kssandra.ksd_ws.validation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ValidationUtils {

	public static boolean validDateTime(String dateTime, String format) {

		try {
			DateTimeFormatter DATE_WITH_TIME_FORMAT = DateTimeFormatter.ofPattern(format);
			LocalDateTime.parse(dateTime, DATE_WITH_TIME_FORMAT);

		} catch (Exception ex) {
			return false;
		}
		return true;

	}

}
