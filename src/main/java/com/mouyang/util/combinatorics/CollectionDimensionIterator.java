package com.mouyang.util.combinatorics;

import java.util.*;

public class CollectionDimensionIterator implements Iterator<Object[]> {
	
	private PeekableIterator[] iterators;
	private boolean hasRolledOver = false;
	
	/**
	 * Implementation of iterator that resets when the current iteration runs out of elements. 
	 */
	@SuppressWarnings("rawtypes")
	private class RolloverIterator implements Iterator {
		private final Collection collection;
		private Iterator iterator;
		
		public RolloverIterator(Collection collection) {
			this.collection = collection;
			iterator = collection.iterator();
		}
		
		/**
		 * Once the end of the iterator has been reached, the iterator gets reset.  The return value reflects whether 
		 * or not the iterator has run out of elements.
		 */
		@Override
		public boolean hasNext() {
			boolean hasNext = iterator.hasNext();
			if (!hasNext) {
				iterator = collection.iterator();
			}
			return hasNext;
		}
		
		/**
		 * This implementation will never throw a NoSuchElementException because hasNext will rollover when an iterator 
		 * runs out of elements.
		 */
		@Override
		public Object next() {
			return iterator.next();
		}
	}
	
	/**
	 * Decorator for an existing iterator where a value can be retrived without moving the iterator cursor ahead.  This 
	 * is done with the peek method.
	 */
	@SuppressWarnings("rawtypes")
	private class PeekableIterator implements Iterator {
		private Iterator iterator;
		private Object currentValue;
		
		public PeekableIterator(Iterator iterator) {
			this.iterator = iterator;
		}
		
		/**
		 * Delegate to iterator.
		 */
		@Override
		public boolean hasNext() {
			return this.iterator.hasNext();
		}
		
		/**
		 * Delegate to iterator and store the value.
		 */
		@Override
		public Object next() {
			return currentValue = this.iterator.next();
		}
		
		/**
		 * The reason of existence for this class.: get the current iterator value without advancing the cursor.
		 */
		public Object peek() {
			return currentValue;
		}
	}
	
	public CollectionDimensionIterator(@SuppressWarnings("rawtypes") Collection[] collections) {
		if (null == collections || 0 == collections.length) {
			throw new IllegalArgumentException("Must have at least one element.");
		}
		final int len = collections.length;
		this.iterators = new PeekableIterator[len];
		this.iterators[0] = new PeekableIterator(new RolloverIterator(collections[0]));
		for (int i = 1; i < len; i++) {
			this.iterators[i] = new PeekableIterator(new RolloverIterator(collections[i]));
			this.iterators[i].next();
		}
	}
	
	@Override
	public boolean hasNext() {
		if (hasRolledOver) {
			return false;
		}
		boolean hasNext = false;
		for (int i = 0; i < iterators.length; i++) {
			if (iterators[i].hasNext()) {
				iterators[i].next();
				hasNext = true;
				break;
			} else {
				iterators[i].next();
			}
		}
		hasRolledOver = !hasNext;
		return hasNext;
	}

	@Override
	public Object[] next() {
		if (hasRolledOver) {
			throw new NoSuchElementException("");
		}
		Object[] next = new Object[iterators.length];
		for (int i = 0; i < iterators.length; i++) {
			next[i] = iterators[i].peek();
		}
		return next;
	}

}
