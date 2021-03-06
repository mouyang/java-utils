package com.mouyang.util.lang;

import static com.mouyang.util.VarArgs.nullSafe;

import java.util.function.BooleanSupplier;

/**
 * This construct intends to express the intention to not do something under certain conditions.
 * <p>
 * This is analogous to the postfix usage of <code>unless</code> keyword in Perl.  If any of the 
 * exception cases are satisfied, then the Runnable block will not execute.  Providing no exception cases will result 
 * in the Runnable block being executed.
 * </p>
 * <p>
 * For example, the following structures which are all equivalent by De Morgan's Law
 * </p>
 * <blockquote><pre>
 * if (condition1 || condition2 || ...) {
 * } else {
 *     // do something
 * }
 * 
 * if (!(condition1 || condition2 || ...)) {
 *     // do something
 * }
 * 
 * if (!condition1 &amp;&amp; !condition2 &amp;&amp; ...) {
 *     // do something
 * }
 * </pre></blockquote>
 * 
 * would be replaced by 
 * 
 * <blockquote><pre>
 * _do( () -&gt; { 
 *     // do something
 * }).unless(
 *       condition1
 *     , condition2
 * ); 
 * </pre></blockquote>
 * 
 * @author Matthew
 *
 */
public class DoUnlessExpression {

	private final Runnable runnable;

	private DoUnlessExpression(Runnable runnable) {
		this.runnable = runnable;
	}

	public static DoUnlessExpression _do(Runnable runnable) {
		return new DoUnlessExpression(runnable);
	}

	public void unless(BooleanSupplier... booleanSuppliers) {
		for (BooleanSupplier booleanSupplier : nullSafe(booleanSuppliers)) {
			if (booleanSupplier.getAsBoolean()) {
				return;
			}
		}
		runnable.run();
	}
}
