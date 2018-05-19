package com.mouyang.util.gof;

import static com.mouyang.util.VarArgs.nullSafe;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableMap;
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.copyOf;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * Setting a value or defining a behaviour for a particular enum value can be done by adding a new method to the enum. 
 * This however has limitations because Java enums are final Singletons defined at compile time.
 * 
 * <ol>
 * <li>No Runtime Enhancements - A value/behaviour depend on components are not initialized at compile time.</li>
 * <li>Inablity to Separate Concerns - Even if enums could handle runtime enhancements, it may not be desireable to 
 * encapsulate everything into an enum because it exposes details that otherwise should not be.</li>
 * <li>Low Cohesion - It may not be worth encapsulating a value/behaviour in an enum if it is not used in a large 
 * number of components.</li>
 * </ol>
 * 
 * EnumVisitor is a single-dispatch adaption of the Visitor design pattern that addresses these issues. It does not 
 * require an enum to implement the visit methods, which would be impossible to do not withstanding reflection for an 
 * enum outside a developer's control anyway. Handlers are defined for enum values through a Builder class. All 
 * possible values of an enum must be defined by default for a visitor, but exceptions can be explicitly defined in 
 * case there is a valid reason for it.
 * 
 * The initial intent of EnumVisitor was to process @FunctionalInterface instances. A @FunctionalInterface however 
 * cannot be used as method arguments, and cannot be executed directly. As a workaround, only a limited set of 
 * @FunctionalInterfaces are currently supported by this: Runnable, Consumer, Supplier, Function, BiFunction, 
 * Predicate, BiPredicate.
 * 
 * @author Matthew
 *
 * @param <E>
 * @param <H>
 */
public class EnumVisitor<E extends Enum<E>, H> {
	public Map<E, H> handlers;
	
	private EnumVisitor(Map<E, H> handlers) {
		this.handlers = handlers;
	}
	
	public H visit(E e) {
		return handlers.get(e);
	}
	
	public static class AbstractEnumVisitorBuilderFactory {
		public static <E extends Enum<E>, H> EnumVisitorBuilder<E, H> newInstance(Class<E> enumClass, Class<H> handlerClass) {
			return newInstance(enumClass, handlerClass, null);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public static <E extends Enum<E>, H> EnumVisitorBuilder<E, H> newInstance(Class<E> enumClass, Class<H> handlerClass, E[] exceptions) {
			if (Runnable.class.isAssignableFrom(handlerClass)) {
				return new RunnableEnumVisitorBuilder(exceptions, enumClass);
			} else if (Consumer.class.isAssignableFrom(handlerClass)) {
				return new ConsumerEnumVisitorBuilder(exceptions, enumClass);
			} else if (Supplier.class.isAssignableFrom(handlerClass)) {
				return new SupplierEnumVisitorBuilder(exceptions, enumClass);
			} else if (Function.class.isAssignableFrom(handlerClass)) {
				return new FunctionEnumVisitorBuilder(exceptions, enumClass);
			} else if (BiFunction.class.isAssignableFrom(handlerClass)) {
				return new BiFunctionEnumVisitorBuilder(exceptions, enumClass);
			} else if (Predicate.class.isAssignableFrom(handlerClass)) {
				return new PredicateEnumVisitorBuilder(exceptions, enumClass);
			} else if (BiPredicate.class.isAssignableFrom(handlerClass)) {
				return new BiPredicateEnumVisitorBuilder(exceptions, enumClass);
			}
			throw new IllegalArgumentException("unsupported handlerClass");
		}
		
		private static class RunnableEnumVisitorBuilder<E extends Enum<E>> extends EnumVisitorBuilder<E, Runnable> {
			public RunnableEnumVisitorBuilder(E[] exceptions, Class<E> enumClass) {
				super(exceptions, enumClass);
			}
		}
		
		private static class ConsumerEnumVisitorBuilder<E extends Enum<E>> extends EnumVisitorBuilder<E, Consumer<?>> {
			public ConsumerEnumVisitorBuilder(E[] exceptions, Class<E> enumClass) {
				super(exceptions, enumClass);
			}
		}
		
		private static class SupplierEnumVisitorBuilder<E extends Enum<E>> extends EnumVisitorBuilder<E, Supplier<?>> {
			public SupplierEnumVisitorBuilder(E[] exceptions, Class<E> enumClass) {
				super(exceptions, enumClass);
			}
		}
		
		private static class FunctionEnumVisitorBuilder<E extends Enum<E>> extends EnumVisitorBuilder<E, Function<?, ?>> {
			public FunctionEnumVisitorBuilder(E[] exceptions, Class<E> enumClass) {
				super(exceptions, enumClass);
			}
		}
		
		private static class BiFunctionEnumVisitorBuilder<E extends Enum<E>> extends EnumVisitorBuilder<E, BiFunction<?, ?, ?>> {
			public BiFunctionEnumVisitorBuilder(E[] exceptions, Class<E> enumClass) {
				super(exceptions, enumClass);
			}
		}
		
		private static class PredicateEnumVisitorBuilder<E extends Enum<E>> extends EnumVisitorBuilder<E, Predicate<?>> {
			public PredicateEnumVisitorBuilder(E[] exceptions, Class<E> enumClass) {
				super(exceptions, enumClass);
			}
		}
		
		private static class BiPredicateEnumVisitorBuilder<E extends Enum<E>> extends EnumVisitorBuilder<E, BiPredicate<?, ?>> {
			public BiPredicateEnumVisitorBuilder(E[] exceptions, Class<E> enumClass) {
				super(exceptions, enumClass);
			}
		}
	}
	
	/**
	 * Stores up handlers, disallow enum values which were initially declared as exceptional, verify that handlers 
	 * exist for all desired values before creating an EnumVisitor.
	 * 
	 * @author Matthew
	 *
	 * @param <E>
	 * @param <H>
	 */
	public static abstract class EnumVisitorBuilder<E extends Enum<E>, H> {
		private Map<E, H> handlers = new HashMap<>();
		private EnumSet<E> allowableValues;
		
		public EnumVisitorBuilder(E[] exceptions, Class<E> enumClass) {
			this(nullSafe(exceptions), enumClass);
		}
		
		private EnumVisitorBuilder(Collection<E> exceptions, Class<E> enumClass) {
			this.allowableValues = (null == exceptions || exceptions.isEmpty()) 
				? copyOf(asList(enumClass.getEnumConstants()))
				: complementOf(copyOf(exceptions));
		}
		
		public EnumVisitorBuilder<E, H> addHandler(E e, H h) {
			if (!allowableValues.contains(e)) {
				throw new IllegalArgumentException(format("cannot add a handler for exception value '%s'", e));
			}
			handlers.put(e, h);
			return this;
		}
		
		public EnumVisitor<E, H> build() {
			if (!handlers.keySet().containsAll(allowableValues)) {
				throw new RuntimeException(format("add handlers for the following enum values ('%s')",
					allowableValues.stream()
						.filter(key -> !handlers.containsKey(key))
						.map(e -> e.name())
						.collect(Collectors.joining(","))
				));
			}
			return new EnumVisitor<E, H>(unmodifiableMap(handlers));
		}
	}
}
