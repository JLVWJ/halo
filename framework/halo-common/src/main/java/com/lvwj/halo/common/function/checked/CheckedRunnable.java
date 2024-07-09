package com.lvwj.halo.common.function.checked;

/**
 * 受检的 runnable
 */
@FunctionalInterface
public interface CheckedRunnable {

	/**
	 * Run this runnable.
	 *
	 * @throws Throwable CheckedException
	 */
	void run() throws Throwable;

}
