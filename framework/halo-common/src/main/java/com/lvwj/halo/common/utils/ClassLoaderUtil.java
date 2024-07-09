package com.lvwj.halo.common.utils;

/**
 *
 * @author lvwj
 * @date 2022-08-11 18:07
 */
public class ClassLoaderUtil {

  public static ClassLoader getContextClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }

  public static ClassLoader getSystemClassLoader() {
    return ClassLoader.getSystemClassLoader();
  }

  public static ClassLoader getClassLoader() {
    ClassLoader classLoader = getContextClassLoader();
    if (classLoader == null) {
      classLoader = ClassLoaderUtil.class.getClassLoader();
      if (null == classLoader) {
        classLoader = getSystemClassLoader();
      }
    }
    return classLoader;
  }
}
