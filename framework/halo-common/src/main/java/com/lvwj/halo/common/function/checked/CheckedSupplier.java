package com.lvwj.halo.common.function.checked;

import org.springframework.lang.Nullable;

/**
 * 受检的 Supplier
 */
@FunctionalInterface
public interface CheckedSupplier<T> {

	/**
	 * Run the Supplier
	 *
	 * @return T
	 * @throws Throwable CheckedException
	 */
	@Nullable
	T get() throws Throwable;

}
