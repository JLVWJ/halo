package com.lvwj.halo.extension.annotation;

import com.lvwj.halo.common.constants.ExtensionConstant;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * because {@link Extension} only supports single coordinates,
 * this annotation is a supplement to {@link Extension} and supports multiple coordinates
 *
 * @author wangguoqiang wrote on 2022/10/10 12:19
 * @version 1.0
 * @see Extension
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Component
public @interface Extensions {

    String[] business() default ExtensionConstant.DEFAULT_BUSINESS;

    String[] useCase() default ExtensionConstant.DEFAULT_USE_CASE;

    String[] scenario() default ExtensionConstant.DEFAULT_SCENARIO;

    /**
     * 扩展点顺序，获取列表的时候排序使用。参照Spring的order，越小排序越靠前
     */
    int order() default Ordered.LOWEST_PRECEDENCE;

    /**
     * 扩展节点概述
     */
    String desc() default "";

    Extension[] value() default {};

}
