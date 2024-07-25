package com.lvwj.halo.rocketmq.annotation;

import com.lvwj.halo.core.domain.event.IntegrationEvent;
import org.apache.rocketmq.client.impl.CommunicationMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RocketMQProducer {

    /**
     * 是否可用
     */
    String enable() default "true";

    /**
     * MQ topic
     */
    String topic();

    /**
     * MQ tag
     */
    String tag();

    /**
     * 消息Class(继承IntegrationEvent)
     */
    Class<? extends IntegrationEvent> msg();

    /**
     * key会存入消息头，当messageMode=ORDER时，key同时会作为分片键来使用
     */
    String key() default "";

    /**
     * 延迟级别
     */
    String delayLevel() default "-1";

    /**
     * 发送超时时间
     */
    long timeout() default 3000;

    /**
     * 发送模式(SYNC, ASYNC, ONEWAY)
     */
    CommunicationMode communicationMode() default CommunicationMode.SYNC;

    /**
     * 消息模式(NORMAL, ORDER)
     */
    MessageMode messageMode() default MessageMode.NORMAL;

    /**
     * 消息体包装消息头
     */
    boolean bodyWithHeader() default false;
}

