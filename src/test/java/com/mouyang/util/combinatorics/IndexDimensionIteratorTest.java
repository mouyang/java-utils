package com.mouyang.util.combinatorics;

import static org.testng.Assert.*;

import java.util.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class IndexDimensionIteratorTest {
	@DataProvider
	public Iterator<Object[]> dimensions() {
		List<Object[]> list = new ArrayList<>();
		list.add(new Object[] {10, 10, 10});
		list.add(new Object[] {3, 4, 5});
		return list.iterator();
	}
	
	@Test(dataProvider = "dimensions")
	public void indexIteration(int[] dimensions) {
		IndexDimensionIterator di = new IndexDimensionIterator(dimensions);
		for (long i = 0; i < calculateMaximumIterations(dimensions); i++) {
			assertTrue(di.hasNext());
			int[] next = di.next();
			assertEquals(next.length, dimensions.length);
			assertEquals(calculateValue(next, dimensions), i);
		}
		assertFalse(di.hasNext());
		try {
			di.next();
			fail();
		} catch (NoSuchElementException e) {
		}
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test(dataProvider = "dimensions")
	public void collectionIteration(int[] dimensions) {
		final int len = dimensions.length;
		Collection[] collections = new Collection[len];
		for (int i = 0; i < len; i++) {
			collections[i] = new ArrayList();
			for (int j = 0; j < dimensions[i]; j++) {
				collections[i].add(j);
			}
		}
		CollectionDimensionIterator di = new CollectionDimensionIterator(collections);
		for (long i = 0; i < calculateMaximumIterations(dimensions); i++) {
			assertTrue(di.hasNext());
			Object[] next = di.next();
			assertEquals(next.length, dimensions.length);
			assertEquals(calculateValue(next, dimensions), i);
		}
		assertFalse(di.hasNext());
		try {
			di.next();
			fail();
		} catch (NoSuchElementException e) {
		}
	}

	private long calculateValue(Object[] counters, int[] coefficients) {
		long value = 0;
		assertEquals(counters.length, coefficients.length);
		for (int i = coefficients.length - 1; i >= 0; i--) {
			value = value * coefficients[i] + (Integer)counters[i];
		}
		return value;
	}

	private long calculateValue(int[] counters, int[] coefficients) {
		long value = 0;
		assertEquals(counters.length, coefficients.length);
		for (int i = coefficients.length - 1; i >= 0; i--) {
			value = value * coefficients[i] + counters[i];
		}
		return value;
	}
	
	private long calculateMaximumIterations(int[] sizes) {
		long max = 1;
		for (int size : sizes) {
			max *= size;
		}
		return max;
	}
}
