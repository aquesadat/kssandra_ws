package com.kssandra.ksd_ws.validation;

import org.apache.commons.lang3.EnumUtils;

import com.kssandra.ksd_ws.enums.IntervalEnum;

public class Test {

	public static void main(String[] args) {
		
		System.out.println(EnumUtils.isValidEnum(IntervalEnum.class, "_15m"));
		
		System.out.println(EnumUtils.getEnum(IntervalEnum.class, "_15m"));

	}

}
