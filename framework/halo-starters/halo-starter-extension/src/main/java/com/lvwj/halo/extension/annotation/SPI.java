package com.lvwj.halo.extension.annotation;

import java.lang.annotation.*;


@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SPI {

  /**
   * 扩展点描述，非必填
   */
  String desc() default "";

  /**
   * 场景ID是否允许重复，默认不允许
   */
  boolean repeatable() default false;
}