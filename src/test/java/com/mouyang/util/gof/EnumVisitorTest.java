package com.mouyang.util.gof;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertFalse;

import java.util.List;
import java.util.function.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.mouyang.util.gof.EnumVisitor.AbstractEnumVisitorBuilderFactory;

public class EnumVisitorTest {
	
	private enum TestEnum { A, B }
	
	private static List<Class<?>> supportedClasses = asList(
		Runnable.class, Consumer.class, Supplier.class, Function.class, BiFunction.class, Predicate.class, BiPredicate.class);

	@DataProvider
	public Object[][] supportedClasses() {
		int len = supportedClasses.size();
		Object[][] objects = new Object[len][];
		for (int i = 0; i < len; i++) {
			objects[i] = new Object[] { supportedClasses.get(i) };
		}
		return objects;
	}
	
	@Test(dataProvider = "supportedClasses")
	public void noExceptions(Class<?> _class) {
		AbstractEnumVisitorBuilderFactory.newInstance(TestEnum.class, _class)
			.addHandler(TestEnum.A, null)
			.addHandler(TestEnum.B, null)
			.build();
	}
	
	@Test(dataProvider = "supportedClasses")
	public void exceptions(Class<?> _class) {
		AbstractEnumVisitorBuilderFactory.newInstance(TestEnum.class, _class, new TestEnum[] {TestEnum.A})
			.addHandler(TestEnum.B, null)
			.build();
	}
	
	@Test(dataProvider = "supportedClasses", expectedExceptions = RuntimeException.class)
	public void noExceptions_missingValue(Class<?> _class) {
		AbstractEnumVisitorBuilderFactory.newInstance(TestEnum.class, _class, new TestEnum[] {TestEnum.A})
			.build();
	}
	
	@Test(dataProvider = "supportedClasses", expectedExceptions = IllegalArgumentException.class)
	public void addHandlerForExceptionValue(Class<?> _class) {
		AbstractEnumVisitorBuilderFactory.newInstance(TestEnum.class, _class, new TestEnum[] {TestEnum.A})
			.addHandler(TestEnum.A, null);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void unsupportedHandlerClass() {
		assertFalse(supportedClasses.contains(Object.class));
		AbstractEnumVisitorBuilderFactory.newInstance(TestEnum.class, Object.class);
	}
}
