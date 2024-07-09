package com.lvwj.halo.common.function.checked;

import org.springframework.lang.Nullable;

/**
 * 受检的 Consumer
 */
@FunctionalInterface
public interface CheckedConsumer<T> {

	/**
	 * Run the Consumer
	 *
	 * @param t T
	 * @throws Throwable UncheckedException
	 */
	@Nullable
	void accept(@Nullable T t) throws Throwable;

}
