package com.mouyang.util;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.List;

public class VarArgs {

	public static <T> List<T> nullSafe(T... ts) {
		return asList(ts).stream().filter(t -> t != null).collect(toList());
	}
}
