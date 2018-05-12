package com.mouyang.util.combinatorics;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This form of dimension iterator is useful for Lists and primitive arrays.  It can't be used for java.util.Collection 
 * because not all Collections have an index (example java.util.Set).
 * 
 * @author Matthew
 *
 */
public class IndexDimensionIterator implements Iterator<int[]> {
	
	private final int[] sizes;
	private int[] currentIteration;
	
	/**
	 * @param lengths
	 * @throws IllegalArgumentException if lengths is null or is an empty array because there is nothing to iterate over.
	 */
	public IndexDimensionIterator(int[] lengths) {
		if (null == lengths || 0 == lengths.length) {
			throw new IllegalArgumentException("Must have at least one element");
		}
		final int len = lengths.length;
		this.sizes = lengths;
		this.currentIteration = new int[len];
		// This is done so that the first call to next will return (0, 0, 0, ...)
		this.currentIteration[0] = -1;
	}
	
	@Override
	public boolean hasNext() {
		for (int i = 0; i < sizes.length; i++) {
			if (sizes[i] - 1 != currentIteration[i]) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int[] next() {
		for (int i = 0; i < sizes.length; i++) {
			if (sizes[i] == ++currentIteration[i]) {
				// rollover case
				currentIteration[i] = 0;
			} else {
				return currentIteration;
			}
		}
		throw new NoSuchElementException("");
	}
}