package com.lvwj.halo.core.design.filter;

/**
 * 责任链
 *
 * @author lvwj
 * @date 2022-08-22 15:20
 */
public class FilterChain<T> {

  private FilterInvoker<T> header;

  public void doFilter(T context) {
    header.invoke(context);
  }

  public void setHeader(FilterInvoker<T> header) {
    this.header = header;
  }
}
