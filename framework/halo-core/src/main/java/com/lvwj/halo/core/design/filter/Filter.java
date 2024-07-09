package com.lvwj.halo.core.design.filter;

/**
 * @author lvwj
 * @date 2022-08-22 15:19
 */
public interface Filter<T> {

  void doFilter(T context, FilterInvoker<T> nextFilter);

}
