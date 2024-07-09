package com.lvwj.halo.common.function.checked;

import org.springframework.lang.Nullable;

/**
 * 受检的 Callable
 */
@FunctionalInterface
public interface CheckedCallable<T> {

	/**
	 * Run this callable.
	 *
	 * @return result
	 * @throws Throwable CheckedException
	 */
	@Nullable
	T call() throws Throwable;
}
