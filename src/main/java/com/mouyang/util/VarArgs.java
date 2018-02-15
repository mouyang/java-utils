package com.mouyang.util;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.List;

public class VarArgs {

	@SafeVarargs
	public static <T> List<T> nullSafe(T... ts) {
		return (ts == null) ? emptyList() : asList(ts).stream().filter(t -> t != null).collect(toList());
	}
}
