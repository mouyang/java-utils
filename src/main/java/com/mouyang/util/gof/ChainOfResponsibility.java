package com.mouyang.util.gof;

import static java.util.stream.Collectors.toList;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * This implementation of the Chain Of Responsibility pattern will allow addition of any number of Suppliers before any 
 * operation that iterates through the Suppliers.
 * 
 * A precondition can be optionally specified in conjunction with a Supplier.  If a precondition is not satisfied, the 
 * Supplier does not get applied.  Preconditions are not necessary; equivalent behaviour can be achieved in a Supplier 
 * by returning a value that does not satisfy the terminating condition.  They are provided to simplify the Supplier.
 * 
 * @author Matthew
 *
 * @param <T>
 */
public class ChainOfResponsibility<T> {
	private HandlerListState handlers = new ModifiableHandlerListState();
	private final Predicate<T> terminatingCondition;
	
	/**
	 * This class implements a subset of the List methods needed by this class.
	 * 
	 * As an implemetation note, this was implemented as an abstract class instead of an interface in order to prevent 
	 * exposing this class externally.  Java 8 does not have a concept of private interfaces.
	 */
	private abstract class HandlerListState {
		private final List<Handler> handlers;
		
		/**
		 * @return A list that cannot be modified, an in particular appended to.
		 */
		abstract HandlerListState frozenList();
		
		private HandlerListState(List<Handler> handlers) {
			this.handlers = handlers;
		}
		
		void add(Handler t) {
			handlers.add(t);
		}
		
		/**
		 * Returns a list of the suppliers without changing the object state.
		 */
		List<Handler> get() {
			return handlers;
		}
	}
	
	private class ModifiableHandlerListState extends HandlerListState {
		/**
		 * Start with an empty list.
		 */
		public ModifiableHandlerListState() {
			super(new ArrayList<>());
		}
		
		/**
		 * Transitions to an unmodifiable state.
		 */
		HandlerListState frozenList() {
			return (ChainOfResponsibility.this).new UnmodifiableHandlerListState(get());
		}
	}

	private class UnmodifiableHandlerListState extends HandlerListState {
		/**
		 * Internally implemented with Collections.unmodifiableList, this will prevent any further modifications.  
		 * Calling add in this state will result in an UnsupportedOperationException because the object itself returned 
		 * by unmodifiableList will throw that exception. 
		 */
		UnmodifiableHandlerListState(List<Handler> suppliers) {
			super(Collections.unmodifiableList(suppliers));
		}
		
		/**
		 * As this is a terminating state, this implementation will return itself.
		 */
		HandlerListState frozenList() {
			return this;
		}
	}
	
	private class Handler {
		private final BooleanSupplier precondition;
		private final Supplier<T> supplier;
		
		/**
		 * The default implementation is to always execute the supplier.
		 * 
		 * @param supplier
		 */
		public Handler(Supplier<T> supplier) {
			this(() -> true, supplier);
		}
		
		public Handler(BooleanSupplier precondition, Supplier<T> supplier) {
			this.precondition = precondition;
			this.supplier = supplier;
		}
		
		public BooleanSupplier getPrecondition() {
			return precondition;
		}
		
		public Supplier<T> getSupplier() {
			return supplier;
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
		this.handlers.add(new Handler(supplier));
	}
	
	public void add(BooleanSupplier precondition, Supplier<T> supplier) {
		this.handlers.add(new Handler(precondition, supplier));
	}
	
	/* Separate the evaluation of the precondition and the supplier.  This allows for skipping evaluation of Suppliers 
	 * that do not satisfy their precondition.
	 */
	private Stream<T> filteredSuppliers() {
		return (this.handlers = this.handlers.frozenList()).get().stream()
			.filter(h -> h.getPrecondition().getAsBoolean())
			.map(h -> h.getSupplier().get())
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
