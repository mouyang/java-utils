package com.mouyang.util.perl;

import static com.mouyang.util.VarArgs.nullSafe;

import java.util.List;
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
		List<BooleanSupplier> nullSafeSuppliers;
		if (null == booleanSuppliers || (nullSafeSuppliers = nullSafe(booleanSuppliers)).isEmpty()) {
			runnable.run();
			return;
		}
		for (BooleanSupplier nullSafeSupplier : nullSafeSuppliers) {
			if (nullSafeSupplier.getAsBoolean()) {
				return;
			}
		}
		runnable.run();
	}
}
