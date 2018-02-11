package com.mouyang.util.function;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class PredicateFactory {
	
	@SuppressWarnings("rawtypes")
	private static final Predicate alwaysTrue = x -> true;

	/**
	 * Null-safe version of Predicate<T>.and(Predicate<T>).  Null inputs will be ignored.
	 * @param predicates
	 * @return predicate that always returns true if no non-null predicates are provided.  non-null predicates joined 
	 * by Predicate<T>.and(Predicate<T>) otherwise.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Predicate<T> allOf(Predicate<T>... predicates) {
		return composite((t, u) -> t.and(u), predicates);
	}

	/**
	 * Null-safe version of Predicate<T>.or(Predicate<T>).  Null inputs will be ignored.
	 * @param predicates
	 * @return predicate that always returns true if no non-null predicates are provided.  non-null predicates joined 
	 * by Predicate<T>.or(Predicate<T>) otherwise.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Predicate<T> anyOf(Predicate<T>... predicates) {
		return composite((t, u) -> t.or(u), predicates);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> Predicate<T> composite(BiFunction<Predicate<T>, Predicate<T>, Predicate<T>> biFunction, Predicate<T>... predicates) {
		List<Predicate<T>> nonNullPredicates;
		if (0 == predicates.length 
				|| (nonNullPredicates = asList(predicates).stream().filter(p -> p != null).collect(toList())).isEmpty()) {
			return alwaysTrue;
		}
		Predicate<T> composite = nonNullPredicates.get(0);
		for (int index = 1; index < nonNullPredicates.size(); index++) {
			composite = biFunction.apply(composite, nonNullPredicates.get(index)); 
		}
		return composite;
	}
 }