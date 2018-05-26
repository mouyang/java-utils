package com.mouyang.util.lang;

import static com.mouyang.util.lang.WithExpression._with;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.function.Consumer;

import org.testng.annotations.Test;

public class WithExpressionTest {
	
	private interface TestMock {
		public void run(int i);
	}
	
	@Test
	public void withExpression_consume() {
		TestMock testMock = mock(TestMock.class);
		final int testValue = 1;
		Consumer<Integer> c = (i) -> testMock.run(i);
		_with(testValue).consume(c);
		verify(testMock).run(testValue);
	}
}
