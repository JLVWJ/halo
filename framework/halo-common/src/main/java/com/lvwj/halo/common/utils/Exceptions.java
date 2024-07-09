
package com.lvwj.halo.common.utils;


import com.lvwj.halo.common.exceptions.BusinessException;
import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

/**
 * 异常处理工具类
 *
 * @author L.cm
 */
public class Exceptions {

  /**
   * 将CheckedException转换为UncheckedException.
   *
   * @param throwable Throwable
   * @return {RuntimeException}
   */
  public static RuntimeException unchecked(Throwable throwable) {
    Throwable e = unwrap(throwable);
    if (e instanceof BusinessException) {
      return (BusinessException) e;
    } else if (e instanceof IllegalAccessException || e instanceof NoSuchMethodException) {
      return new RuntimeException(e);
    } else if (e instanceof RuntimeException) {
      return (RuntimeException) e;
    } else if (e instanceof InterruptedException) {
      Thread.currentThread().interrupt();
    } else if (e instanceof Error) {
      throw (Error) e;
    }
    return runtime(e);
  }

  /**
   * 不采用 RuntimeException 包装，直接抛出，使异常更加精准
   *
   * @param throwable Throwable
   * @param <T>       泛型标记
   * @return Throwable
   * @throws T 泛型
   */
  @SuppressWarnings("unchecked")
  private static <T extends Throwable> T runtime(Throwable throwable) throws T {
    throw (T) throwable;
  }

  /**
   * 代理异常解包
   *
   * @param wrapped 包装过得异常
   * @return 解包后的异常
   */
  public static Throwable unwrap(Throwable wrapped) {
    Throwable unwrapped = wrapped;
    while (true) {
      if (unwrapped instanceof InvocationTargetException) {
        unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
      } else if (unwrapped instanceof UndeclaredThrowableException) {
        unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
      } else if (unwrapped instanceof ExecutionException || unwrapped instanceof CompletionException) {
        unwrapped = unwrapped.getCause();
      } else {
        return unwrapped;
      }
    }
  }

  /**
   * 将StackTrace转化为String.
   *
   * @param t Throwable
   * @return {String}
   */
  @SneakyThrows
  public static String getStackTrace(Throwable t) {
    @Cleanup final ByteArrayOutputStream out = new ByteArrayOutputStream();
    @Cleanup final PrintStream ps = new PrintStream(out);
    t.printStackTrace(ps);
    ps.flush();
    return out.toString();
  }
}
