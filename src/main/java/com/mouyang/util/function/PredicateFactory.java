package com.mouyang.util.function;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.List;
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
		if (0 == predicates.length) {
			return alwaysTrue;
		}
		List<Predicate<T>> nonNullPredicates = asList(predicates).stream().filter(p -> p != null).collect(toList());
		if (nonNullPredicates.isEmpty()) {
			return alwaysTrue;
		}
		Predicate<T> composite = nonNullPredicates.get(0);
		for (int i = 1; i < nonNullPredicates.size(); i++) {
			composite = composite.and(nonNullPredicates.remove(i)); 
		}
		return composite;
	}
 }