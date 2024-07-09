package com.lvwj.halo.extension.annotation;

import com.lvwj.halo.common.constants.ExtensionConstant;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;


@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(Extensions.class)
@Component
public @interface Extension {

  /**
   * 业务线，例如：淘宝、天猫、聚划算
   */
  String business() default ExtensionConstant.DEFAULT_BUSINESS;

  /**
   * 模块或者用例，例如：支付
   */
  String useCase() default ExtensionConstant.DEFAULT_USE_CASE;

  /**
   * 场景，例如兑换券支付、移动支付、线下支付等
   */
  String scenario() default ExtensionConstant.DEFAULT_SCENARIO;

  /**
   * 扩展点顺序，获取列表的时候排序使用。参照Spring的order，越小排序越靠前
   */
  int order() default Ordered.LOWEST_PRECEDENCE;

  /**
   * 扩展节点概述
   */
  String desc() default "";
}

