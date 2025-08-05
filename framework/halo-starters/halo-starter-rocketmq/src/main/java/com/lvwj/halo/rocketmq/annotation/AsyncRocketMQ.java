package com.lvwj.halo.rocketmq.annotation;

import org.apache.rocketmq.client.impl.CommunicationMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsyncRocketMQ {

    String ENABLE_PLACEHOLDER = "${halo.async.rocketmq.enable:true}";

    String TOPIC_PLACEHOLDER = "${halo.async.rocketmq.topic:}";
    String NAME_SERVER_PLACEHOLDER = "${rocketmq.name-server:}";
    String CONSUMER_GROUP_SERVER_PLACEHOLDER = "${spring.application.name:}";

    String MaxReconsumeTimes_PLACEHOLDER = "${halo.async.rocketmq.consumer.maxReconsumeTimes:5}";
    String ConsumeThreadNumber_PLACEHOLDER = "${halo.async.rocketmq.consumer.consumeThreadNumber:20}";
    String PullBatchSize_PLACEHOLDER = "${halo.async.rocketmq.consumer.pullBatchSize:20}";
    String DelayLevelWhenNextConsume_PLACEHOLDER = "${halo.async.rocketmq.consumer.delayLevelWhenNextConsume:1}";
    String SuspendCurrentQueueTimeMillis_PLACEHOLDER = "${halo.async.rocketmq.consumer.suspendCurrentQueueTimeMillis:10000}";
    String SkipWhenException_PLACEHOLDER = "${halo.async.rocketmq.consumer.skipWhenException:false}";

    /**
     * 是否可用
     */
    String enable() default ENABLE_PLACEHOLDER;

    /**
     * nameServer 配置
     */
    String nameServer() default NAME_SERVER_PLACEHOLDER;

    /**
     * MQ topic
     */
    String topic() default TOPIC_PLACEHOLDER;



    /**
     * MQ tag (默认取类名.方法名)
     */
    String tag() default "";

    /**
     * key会存入消息头，当messageMode=ORDER时，key同时会作为分片键来使用
     */
    String key() default "";

    /**
     * 延迟级别
     */
    String delayLevel() default "-1";

    /**
     * 发送模式(SYNC, ASYNC, ONEWAY)
     */
    CommunicationMode communicationMode() default CommunicationMode.SYNC;

    /**
     * 消息模式(NORMAL, ORDER)
     */
    MessageMode messageMode() default MessageMode.NORMAL;

    /**
     * 消费者组
     */
    String consumerGroup() default CONSUMER_GROUP_SERVER_PLACEHOLDER;

    /**
     * 消费者运行的 profile，主要用于发送和消费分离的场景
     */
    String consumerProfile() default "";

    /**
     * consumer thread number.
     */
    String consumeThreadNumber() default ConsumeThreadNumber_PLACEHOLDER;

    /**
     * Max re-consume times.
     *
     * In concurrently mode, -1 means 16;
     * In orderly mode, -1 means Integer.MAX_VALUE.
     */
    String maxReconsumeTimes() default MaxReconsumeTimes_PLACEHOLDER;

    /**
     * 消息拉取批次数量
     */
    String pullBatchSize() default PullBatchSize_PLACEHOLDER;

    /**
     * Message consume retry strategy in concurrently mode.
     *
     * -1,no retry,put into DLQ directly
     * 0,broker control retry frequency
     * >0,client control retry frequency
     */
    String delayLevelWhenNextConsume() default DelayLevelWhenNextConsume_PLACEHOLDER;

    /**
     * The interval of suspending the pull in orderly mode, in milliseconds.
     *
     * The minimum value is 10 and the maximum is 30000.
     */
    String suspendCurrentQueueTimeMillis() default SuspendCurrentQueueTimeMillis_PLACEHOLDER;

    /**
     * 消费异常时是否跳过，不重试
     */
    String skipWhenException() default SkipWhenException_PLACEHOLDER;
}

