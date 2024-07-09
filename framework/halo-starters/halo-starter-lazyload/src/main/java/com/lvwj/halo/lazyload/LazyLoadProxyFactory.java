package com.lvwj.halo.lazyload;

/**
 * 懒加载代理工厂
 *
 * @author lvweijie
 * @date 2023年11月04日 14:56
 */
public interface LazyLoadProxyFactory {
    <T> T createProxyFor(T t);
}
