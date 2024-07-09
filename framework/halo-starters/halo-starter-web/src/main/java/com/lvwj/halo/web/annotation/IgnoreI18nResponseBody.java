package com.lvwj.halo.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 忽略国际化包装web全局响应
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2023-01-05 15:05
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE})
public @interface IgnoreI18nResponseBody {

}
