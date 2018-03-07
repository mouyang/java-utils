package com.mouyang.util.gof;

import static java.util.stream.Collectors.toList;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * This implementation of the Chain Of Responsibility pattern will allow addition of any number of Suppliers before any 
 * operation that iterates through the Suppliers.
 * 
 * @author Matthew
 *
 * @param <T>
 */
public class ChainOfResponsibility<T> {
	private SupplierListState suppliers = new ModifiableSupplierListState();
	private final Predicate<T> terminatingCondition;
	
	/**
	 * This class implements a subset of the List methods needed by this class.
	 * 
	 * As an implemetation note, this was implemented as an abstract class instead of an interface in order to prevent 
	 * exposing this class externally.  Java 8 does not have a concept of private interfaces.
	 */
	private abstract class SupplierListState {
		private final List<Supplier<T>> suppliers;
		
		/**
		 * @return A list that cannot be modified, an in particular appended to.
		 */
		abstract SupplierListState frozenList();
		
		private SupplierListState(List<Supplier<T>> suppliers) {
			this.suppliers = suppliers;
		}
		
		void add(Supplier<T> t) {
			suppliers.add(t);
		}
		
		/**
		 * Returns a list of the suppliers without changing the object state.
		 */
		List<Supplier<T>> get() {
			return suppliers;
		}
	}
	
	private class ModifiableSupplierListState extends SupplierListState {
		/**
		 * Start with an empty list.
		 */
		public ModifiableSupplierListState() {
			super(new ArrayList<>());
		}
		
		/**
		 * Transitions to an unmodifiable state.
		 */
		SupplierListState frozenList() {
			return (ChainOfResponsibility.this).new UnmodifiableSupplierListState(get());
		}
	}

	private class UnmodifiableSupplierListState extends SupplierListState {
		/**
		 * Internally implemented with Collections.unmodifiableList, this will prevent any further modifications.  
		 * Calling add in this state will result in an UnsupportedOperationException because the object itself returned 
		 * by unmodifiableList will throw that exception. 
		 */
		UnmodifiableSupplierListState(List<Supplier<T>> suppliers) {
			super(Collections.unmodifiableList(suppliers));
		}
		
		/**
		 * As this is a terminating state, this implementation will return itself.
		 */
		SupplierListState frozenList() {
			return this;
		}
	}
	
	/**
	 * Default implementation accepts any supplier that does not return null.
	 */
	public ChainOfResponsibility() {
		this(t -> (null != t));
	}
	
	/**
	 * Allows for specification of a terminating condition.  This will override the default implementation; therefore, 
	 * if that behaviour is still desired, it must be accounted for in the terminating condition.
	 */
	public ChainOfResponsibility(Predicate<T> terminatingCondition) {
		this.terminatingCondition = terminatingCondition;
	}
	
	public void add(Supplier<T> supplier) {
		this.suppliers.add(supplier);
	}
	
	public List<Supplier<T>> get() {
		return this.suppliers.get();
	}

	private Stream<T> filteredSuppliers() {
		return (this.suppliers = this.suppliers.frozenList()).get().stream()
			.map(t -> t.get())
			.filter(t -> terminatingCondition.test(t));
	}
	
	public Optional<T> findFirst() {
		return filteredSuppliers().findFirst();
	}
	
	public List<T> findAll() {
		return filteredSuppliers().collect(toList());
	}
	
	public void consumeFirst(Consumer<T> consumer) {
		findFirst().ifPresent(consumer);
	}
	
	public void consumeAll(Consumer<T> consumer) {
		findAll().forEach(consumer);
	}
}
