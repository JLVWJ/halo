package com.lvwj.halo.core.design.filter;

/**
 * @author lvwj
 * @date 2022-08-22 15:20
 */
public interface FilterInvoker<T> {

  default void invoke(T context) {
  }
}

