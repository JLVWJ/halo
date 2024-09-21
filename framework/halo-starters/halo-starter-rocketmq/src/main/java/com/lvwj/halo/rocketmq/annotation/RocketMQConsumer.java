package com.lvwj.halo.rocketmq.annotation;

import org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Component
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RocketMQConsumer {

    String NAME_SERVER_PLACEHOLDER = "${rocketmq.name-server:}";
    String TRACE_TOPIC_PLACEHOLDER = "${rocketmq.consumer.customized-trace-topic:}";

    /**
     * 是否可用
     */
    String enable() default "true";

    /**
     * nameServer 配置
     */
    String nameServer() default NAME_SERVER_PLACEHOLDER;

    /**
     * MQ topic
     */
    String topic() default "";

    /**
     * 消费者组
     */
    String consumerGroup() default "";

    /**
     * 消息拉取批次数量
     */
    String pullBatchSize() default "20";

    /**
     * 消费模式: CONCURRENTLY  or  ORDERLY
     */
    ConsumeMode consumeMode() default ConsumeMode.CONCURRENTLY;

    /**
     * 消息模式: CLUSTERING  or  BROADCASTING
     */
    MessageModel messageModel() default MessageModel.CLUSTERING;

    /**
     * Switch flag instance for message trace.
     */
    String enableMsgTrace() default "true";

    /**
     * The name value of message trace topic.If you don't config,you can use the default trace topic name [RMQ_SYS_TRACE_TOPIC].
     */
    String customizedTraceTopic() default TRACE_TOPIC_PLACEHOLDER;

    /**
     * The namespace of consumer.
     */
    String namespace() default "";

    /**
     * consumer thread number.
     */
    String consumeThreadNumber() default "20";

    /**
     * Max re-consume times.
     *
     * In concurrently mode, -1 means 16;
     * In orderly mode, -1 means Integer.MAX_VALUE.
     */
    String maxReconsumeTimes() default "-1";

    /**
     * Maximum amount of time in minutes a message may block the consuming thread.
     */
    String consumeTimeout() default "15";

    /**
     * Message consume retry strategy in concurrently mode.
     *
     * -1,no retry,put into DLQ directly
     * 0,broker control retry frequency
     * >0,client control retry frequency
     */
    String delayLevelWhenNextConsume() default "0";

    /**
     * The interval of suspending the pull in orderly mode, in milliseconds.
     *
     * The minimum value is 10 and the maximum is 30000.
     */
    String suspendCurrentQueueTimeMillis() default "10000";

    /**
     * Maximum time to await message consuming when shutdown consumer, in milliseconds.
     * The minimum value is 0
     */
    String awaitTerminationMillisWhenShutdown() default "20000";

    String instanceName() default "DEFAULT";

    /**
     * 消费异常时是否跳过，不重试
     */
    boolean skipWhenException() default false;
}

