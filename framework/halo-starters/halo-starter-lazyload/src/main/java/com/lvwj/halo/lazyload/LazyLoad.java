package com.lvwj.halo.lazyload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 懒加载
 *
 * @author lvweijie
 * @date 2023年11月04日 14:56
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LazyLoad {
    String value();
}
