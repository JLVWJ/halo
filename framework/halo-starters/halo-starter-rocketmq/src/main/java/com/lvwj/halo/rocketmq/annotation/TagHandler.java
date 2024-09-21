package com.lvwj.halo.rocketmq.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TagHandler {
    String value() default "";

    /**
     * 消费异常时是否跳过，不重试
     */
    boolean skipWhenException() default false;

    /**
     * 重试次数
     */
    int reconsumeTimes() default -1;

    /**
     * 并发模式适用
     */
    int delayLevelWhenNextConsume() default -1;
}
