package com.mouyang.util.gof;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.testng.annotations.Test;

public class ChainOfResponsibilityTest {
	@Test
	public void defaultSetting() {
		ChainOfResponsibility<Integer> chain = new ChainOfResponsibility<>();
		chain.add(() -> null);
		chain.add(() -> 1);
		chain.add(() -> null);
		chain.add(() -> 2);
		final int findFirst = chain.findFirst().get();
		assertEquals(1, findFirst);
		final List<Integer> findAll = chain.findAll();
		assertThat(findAll, containsInAnyOrder(Arrays.asList(1, 2).toArray()));
	}
	
	@Test
	public void nonDefaultTerminatingCondition() {
		ChainOfResponsibility<Integer> chain = new ChainOfResponsibility<>(x -> x >= 3);
		chain.add(() -> 1);
		chain.add(() -> 2);
		chain.add(() -> 3);
		chain.add(() -> 4);
		final int findFirst = chain.findFirst().get();
		assertEquals(3, findFirst);
		final List<Integer> findAll = chain.findAll();
		assertThat(findAll, containsInAnyOrder(Arrays.asList(3, 4).toArray()));
	}
	
	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void findFirst_addToFrozenList() {
		ChainOfResponsibility<Integer> chain = new ChainOfResponsibility<>();
		chain.add(() -> 1);
		chain.add(() -> 2);
		chain.findFirst().get();
		chain.add(() -> 3);
	}
	
	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void findAll_addToFrozenList() {
		ChainOfResponsibility<Integer> chain = new ChainOfResponsibility<>();
		chain.add(() -> 1);
		chain.add(() -> 2);
		chain.findAll();
		chain.add(() -> 3);
	}
	
	@Test
	public void addAfter_get() {
		ChainOfResponsibility<Integer> chain = new ChainOfResponsibility<>(x -> x >= 3);
		chain.add(() -> 1);
		chain.add(() -> 2);
		chain.get();
		chain.add(() -> 3);
		final int findFirst = chain.findFirst().get();
		assertEquals(3, findFirst);
	}
	
	@Test
	public void consumeFirst() {
		@SuppressWarnings("unchecked") Consumer<Integer> consumer = mock(Consumer.class);
		ChainOfResponsibility<Integer> chain = new ChainOfResponsibility<>();
		chain.add(() -> null);
		chain.add(() -> 1);
		chain.add(() -> null);
		chain.add(() -> 2);
		chain.consumeFirst(consumer);
		verify(consumer).accept(1);
		verify(consumer).accept(any(Integer.class));
	}
	
	@Test
	public void consumeAll() {
		@SuppressWarnings("unchecked") Consumer<Integer> consumer = mock(Consumer.class);
		ChainOfResponsibility<Integer> chain = new ChainOfResponsibility<>();
		chain.add(() -> null);
		chain.add(() -> 1);
		chain.add(() -> null);
		chain.add(() -> 2);
		chain.consumeAll(consumer);
		verify(consumer).accept(1);
		verify(consumer).accept(2);
		verify(consumer, times(2)).accept(any(Integer.class));
	}
}