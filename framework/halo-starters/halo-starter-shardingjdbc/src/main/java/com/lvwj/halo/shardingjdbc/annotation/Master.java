package com.lvwj.halo.shardingjdbc.annotation;

import java.lang.annotation.*;

/**
 * 方法注解：切成主库
 *
 * @author lvweijie
 * @date 2023年12月12日 10:30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Master {
}
