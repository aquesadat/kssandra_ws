package com.kssandra.ksd_ws.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public enum IntervalEnum {

	M15("M15", Arrays.asList(0, 15, 30, 45)), M30("M30", Arrays.asList(0, 30)), M60("M60", Arrays.asList(0));

	private String name;

	private List<Integer> values;

	IntervalEnum(String name, List<Integer> values) {
		this.name = name;
		this.values = values;
	}

	public String getName() {
		return name;
	}

	public List<Integer> getValues() {
		return values;
	}

	public static IntervalEnum fromName(String name) {
		return Stream.of(IntervalEnum.values()).filter(ivl -> ivl.getName().equals(name)).findFirst().orElse(null);
	}

}
