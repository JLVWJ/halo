package com.lvwj.halo.common.utils;


import com.lvwj.halo.common.function.checked.*;

/**
 * 重试工具类
 *
 * @author lvweijie
 * @date 2023年12月11日 12:15
 */
public class RetryUtil {

    private static final long MILLIS = 200; //重试周期默认200毫秒

    /**
     * 重试执行
     *
     * @param retryNum 重试次数
     * @param runnable runnable
     * @author lvweijie
     * @date 2023/12/11 12:40
     */
    public static void exec(int retryNum, CheckedRunnable runnable) {
        exec(retryNum, MILLIS, runnable, null);
    }

    public static void exec(int retryNum, CheckedRunnable runnable, CheckedConsumer<Throwable> exceptionCallback) {
        exec(retryNum, MILLIS, runnable, exceptionCallback);
    }

    /**
     * 重试执行
     *
     * @param retryNum          重试次数
     * @param retryPeriod       重试周期：假设20毫秒，则重试周期分别是20、40、60....
     * @param runnable          runnable
     * @param exceptionCallback 异常回调处理
     * @author lvweijie
     * @date 2023/12/11 12:40
     */
    public static void exec(int retryNum, long retryPeriod, CheckedRunnable runnable, CheckedConsumer<Throwable> exceptionCallback) {
        int i = 0;
        do {
            try {
                runnable.run();
                break;
            } catch (Throwable e) {
                if (null != exceptionCallback) {
                    try {
                        exceptionCallback.accept(e);
                    } catch (Throwable ex) {
                        throw Exceptions.unchecked(e);
                    }
                }

                if (retryNum <= 0 || i == retryNum) {
                    throw Exceptions.unchecked(e);
                }

                i++;
                try {
                    Thread.sleep(i * retryPeriod);
                } catch (InterruptedException ex) {
                    throw Exceptions.unchecked(e);
                }
            }
        } while (i <= retryNum);
    }

    /**
     * 重试执行
     *
     * @param retryNum 重试次数
     * @param supplier supplier
     * @author lvweijie
     * @date 2023/12/11 12:40
     */
    public static <T> T exec(int retryNum, CheckedSupplier<T> supplier) {
        return exec(retryNum, MILLIS, supplier, null);
    }

    public static <T> T exec(int retryNum, CheckedSupplier<T> supplier, CheckedFunction<Throwable, T> exceptionCallback) {
        return exec(retryNum, MILLIS, supplier, exceptionCallback);
    }

    /**
     * 重试执行
     *
     * @param retryNum          重试次数
     * @param retryPeriod       重试周期：假设20毫秒，则重试周期分别是20、40、60....
     * @param supplier          supplier
     * @param exceptionCallback 异常回调处理
     * @author lvweijie
     * @date 2023/12/11 12:40
     */
    public static <T> T exec(int retryNum, long retryPeriod, CheckedSupplier<T> supplier, CheckedFunction<Throwable, T> exceptionCallback) {
        int i = 0;
        do {
            try {
                return supplier.get();
            } catch (Throwable e) {
                if (null != exceptionCallback) {
                    try {
                        return exceptionCallback.apply(e);
                    } catch (Throwable ex) {
                        throw Exceptions.unchecked(e);
                    }
                }

                if (retryNum <= 0 || i == retryNum) {
                    throw Exceptions.unchecked(e);
                }

                i++;
                try {
                    Thread.sleep(i * retryPeriod);
                } catch (InterruptedException ex) {
                    throw Exceptions.unchecked(e);
                }
            }
        } while (i <= retryNum);
        return null;
    }
}
