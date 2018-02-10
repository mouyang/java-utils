package com.mouyang.perl;

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
public class DoUnlessExpr {

	private final Runnable runnable;

	private DoUnlessExpr(Runnable runnable) {
		this.runnable = runnable;
	}

	public static DoUnlessExpr _do(Runnable runnable) {
		return new DoUnlessExpr(runnable);
	}

	public void unless(BooleanSupplier... booleanSuppliers) {
		if (null == booleanSuppliers || 0 == booleanSuppliers.length) {
			runnable.run();
			return;
		}
		for (BooleanSupplier booleanSupplier : booleanSuppliers) {
			if (booleanSupplier.getAsBoolean()) {
				return;
			}
		}
		runnable.run();
	}
}
