package com.lvwj.halo.swagger2.core.annotation;

import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
@ComponentScan(
        basePackages = {"com.lvwj.halo.swagger2.core.toolkit", "com.lvwj.halo.swagger2.core.web"}
)
public @interface EnableDubboSwagger {

}
