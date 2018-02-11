package com.mouyang.util.function;

import static com.mouyang.util.function.PredicateFactory.allOf;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import java.util.function.Predicate;

import org.testng.annotations.Test;

public class PredicateFactoryTest {

	// warning suppression is justified because the predicate should always return true regardless of type
	@Test @SuppressWarnings({"rawtypes", "unchecked"})
	public void allOf_null() {
		Predicate predicate = allOf((Predicate)null);
		assertTrue(predicate.test(null));
		assertTrue(predicate.test(1));
	}

	// warning suppression is justified because the predicate should always return true regardless of type
	@Test @SuppressWarnings({"rawtypes", "unchecked"})
	public void allOf_none() {
		Predicate predicate = allOf();
		assertTrue(predicate.test(null));
		assertTrue(predicate.test(1));
	}

	// warning suppression is justified because the predicate should always return true regardless of type
	@Test @SuppressWarnings({"rawtypes", "unchecked"})
	public void allOf_multipleNulls() {
		Predicate predicate = allOf(null, null, null);
		assertTrue(predicate.test(null));
		assertTrue(predicate.test(1));
	}

	@Test
	public void allOf_single() {
		@SuppressWarnings("unchecked") Predicate<Integer> predicate = allOf(i -> i > 0);
		assertTrue(predicate.test(1));
		assertFalse(predicate.test(-1));
	}

	@Test
	public void allOf_multiple() {
		@SuppressWarnings("unchecked") Predicate<Integer> predicate = allOf(
			i -> i > 0, i -> i < 10);
		assertTrue(predicate.test(1));
		assertFalse(predicate.test(-1));
		assertFalse(predicate.test(10));
	}

	@Test
	public void allOf_singleWithLeadingNull() {
		@SuppressWarnings("unchecked") Predicate<Integer> predicate = allOf(
			null, i -> i > 0);
		assertTrue(predicate.test(1));
		assertFalse(predicate.test(-1));
	}

	@Test
	public void allOf_singleWithTrailingNull() {
		@SuppressWarnings("unchecked") Predicate<Integer> predicate = allOf(
			i -> i > 0, null);
		assertTrue(predicate.test(1));
		assertFalse(predicate.test(-1));
	}

	@Test
	public void allOf_multipleWithLeadingNull() {
		@SuppressWarnings("unchecked") Predicate<Integer> predicate = allOf(
			null, i -> i > 0, i -> i < 10);
		assertTrue(predicate.test(1));
		assertFalse(predicate.test(-1));
		assertFalse(predicate.test(10));
	}

	@Test
	public void allOf_multipleWithTrailingNull() {
		@SuppressWarnings("unchecked") Predicate<Integer> predicate = allOf(
			i -> i > 0, i -> i < 10, null);
		assertTrue(predicate.test(1));
		assertFalse(predicate.test(-1));
		assertFalse(predicate.test(10));
	}

	@Test
	public void allOf_multipleWithNullsInMiddle() {
		@SuppressWarnings("unchecked") Predicate<Integer> predicate = allOf(
			i -> i > 0, null, i -> i < 30, null, null, i -> i < 20, i -> i < 10);
		assertTrue(predicate.test(1));
		assertFalse(predicate.test(-1));
		assertFalse(predicate.test(10));
		assertFalse(predicate.test(30));
		assertFalse(predicate.test(20));
	}
}
