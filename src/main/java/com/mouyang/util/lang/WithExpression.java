package com.mouyang.util.lang;

import java.util.function.Consumer;

/**
 * <p>
 * Compacts an expression into a variable to be used in a scope. This is seen in Python (but with no cleanup as there 
 * is none to do anyway), JavaScript (without the confusing scoping issues), and Java 7+ Closeables.
 * </p><p>
 * This has the side-effect of creating additional (anonymous or otherwise) classes. Equivalent behaviour can be 
 * achieved by either creating a variable in the current scope, or creating a new scope. It is more clear with this 
 * construct that a local variable is no longer in use as it not only reduces its scope but its lifetime as well.
 * </p>
 * <p>
 * The following code blocks are equivalent
 * <blockquote><pre>
 * ...
 * int x = a.b.c.d;
 * ...
 * </pre></blockquote>
 * <blockquote><pre>
 * ...
 * {
 *     int x = a.b.c.d;
 * }
 * ...
 * </pre></blockquote>
 * <blockquote><pre>
 * ...
 * _with(a.b.c.d, (x) -> {
 *     ...
 * });
 * ...
 * </pre></blockquote>
 * </p>
 * @author Matthew
 *
 */
public class WithExpression<T> {
	private T object;
	
	private WithExpression(T object) {
		this.object = object;
	}
	
	public static <T> WithExpression<T> _with(T object) {
		return new WithExpression<>(object);
	}
	
	public void consume(Consumer<T> consumer) {
		consumer.accept(object);
	}
}
