package com.mouyang.perl;

import static com.mouyang.perl.DoUnlessExpr._do;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.function.BooleanSupplier;

import org.testng.annotations.Test;

public class DoUnlessExprTest {

	private interface TestMock {
		public void run();
	}

	@Test
	public void nullArguments() {
		final TestMock testMock = mock(TestMock.class);
		_do(() -> {
			testMock.run();
		}).unless((BooleanSupplier[])null);
		verify(testMock).run();
	}

	@Test
	public void none() {
		final TestMock testMock = mock(TestMock.class);
		_do(() -> {
			testMock.run();
		}).unless();
		verify(testMock).run();
	}
	
	@Test
	public void allFalse() {
		final TestMock testMock = mock(TestMock.class);
		_do(() -> {
			testMock.run();
		}).unless(
			() -> false
			, () -> false
		);
		verify(testMock).run();
	}

	@Test
	public void anyTrue() {
		final TestMock testMock = mock(TestMock.class);
		_do(() -> {
			testMock.run();
		}).unless(
			() -> false
			, () -> true
		);
		verify(testMock, never()).run();
	}

	@Test
	public void allTrue() {
		final TestMock testMock = mock(TestMock.class);
		_do(() -> {
			testMock.run();
		}).unless(
			() -> true
			, () -> true
		);
		verify(testMock, never()).run();
	}
}
