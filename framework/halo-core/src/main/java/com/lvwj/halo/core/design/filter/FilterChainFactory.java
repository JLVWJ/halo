package com.lvwj.halo.core.design.filter;

import cn.hutool.extra.spring.SpringUtil;

/**
 * 责任链工厂
 *
 * @author lvwj
 * @date 2022-08-22 15:20
 */
public class FilterChainFactory {

  public static <T> FilterChain<T> buildFilterChain(Class... filterClsList) {
    FilterInvoker<T> last = new FilterInvoker<T>() {
    };
    FilterChain<T> filterChain = new FilterChain<T>();
    for (int i = filterClsList.length - 1; i >= 0; i--) {
      FilterInvoker<T> next = last;
      Filter<T> filter = (Filter) SpringUtil.getBean(filterClsList[i]);
      last = new FilterInvoker<T>() {
        @Override
        public void invoke(T context) {
          filter.doFilter(context, next);
        }
      };
    }
    filterChain.setHeader(last);
    return filterChain;
  }
}
