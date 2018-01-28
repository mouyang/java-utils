package com.mouyang.perl;

import static com.mouyang.perl.DoUnlessExpr._do;

import org.testng.annotations.Test;

public class DoUnlessExprTest {

	@Test(expectedExceptions = RuntimeException.class)
	public void none() {
		_do(() -> {
			throw new RuntimeException();
		}).unless();
	}
	
	@Test(expectedExceptions = RuntimeException.class)
	public void allFalse() {
		_do(() -> {
			throw new RuntimeException();
		}).unless(
			() -> false
			, () -> false
		);
	}

	@Test
	public void anyTrue() {
		_do(() -> {
			throw new RuntimeException();
		}).unless(
			() -> false
			, () -> true
		);
	}

	@Test
	public void allTrue() {
		_do(() -> {
			throw new RuntimeException();
		}).unless(
			() -> true
			, () -> true
		);
	}
}
