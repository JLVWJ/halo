package com.lvwj.halo.rocketmq.async;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lvwj.halo.common.utils.Exceptions;
import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.common.utils.JsonUtil;
import com.lvwj.halo.rocketmq.annotation.AsyncRocketMQ;
import com.lvwj.halo.rocketmq.annotation.MessageMode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.MDC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
public class AsyncRocketMQConsumerContainer implements InitializingBean, SmartLifecycle {

    @Getter
    protected final Environment environment;
    @Getter
    protected final Object bean;
    @Getter
    protected final Method method;
    @Getter
    protected final AsyncRocketMQ asyncRocketMQ;

    private volatile boolean running;
    private DefaultMQPushConsumer consumer;

    public AsyncRocketMQConsumerContainer(Environment environment, Object bean, Method method, AsyncRocketMQ asyncRocketMQ) {
        this.environment = environment;
        this.bean = bean;
        this.method = method;
        this.asyncRocketMQ = asyncRocketMQ;
    }

    protected String resolve(String value) {
        if (StringUtils.hasText(value)) {
            return this.environment.resolvePlaceholders(value);
        }
        return value;
    }

    protected void doStart() {
        try {
            this.consumer.start();
            log.info("success to start consumer {}", this.consumer);
        } catch (MQClientException e) {
            log.error("failed to start rocketmq consumer {}", this.consumer);
        }
    }

    protected void doShutdown() {
        this.consumer.shutdown();
        log.info("success to shutdown consumer {}", this.consumer);
    }

    protected DefaultMQPushConsumer createConsumer() throws Exception {
        String nameServer = resolve(this.asyncRocketMQ.nameServer());
        String group = resolve(this.asyncRocketMQ.consumerGroup());
        String topic = resolve(this.asyncRocketMQ.topic());
        String tag = getTag(resolve(this.asyncRocketMQ.tag()));
        Integer consumeThreadNumber = Func.toInt(resolve(this.asyncRocketMQ.consumeThreadNumber()), 20);
        Integer pullBatchSize = Func.toInt(resolve(this.asyncRocketMQ.pullBatchSize()), 20);
        Integer maxReconsumeTimes = Func.toInt(resolve(this.asyncRocketMQ.maxReconsumeTimes()), 5);
        Integer delayLevelWhenNextConsume = Func.toInt(resolve(this.asyncRocketMQ.delayLevelWhenNextConsume()), 1);
        Integer suspendCurrentQueueTimeMillis = Func.toInt(resolve(this.asyncRocketMQ.suspendCurrentQueueTimeMillis()), 10000);
        boolean skipWhenException = Func.toBoolean(resolve(this.asyncRocketMQ.skipWhenException()), false);

        MessageMode messageMode = this.asyncRocketMQ.messageMode();

        // 构建 DefaultMQPushConsumer
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        consumer.setNamesrvAddr(nameServer);
        consumer.setConsumerGroup(group);
        consumer.setConsumeThreadMin(consumeThreadNumber);
        consumer.setConsumeThreadMax(consumeThreadNumber);
        consumer.setPullBatchSize(pullBatchSize);
        consumer.setMaxReconsumeTimes(maxReconsumeTimes);
        consumer.subscribe(topic, tag);
        if (messageMode == MessageMode.NORMAL)
            consumer.setMessageListener(new DefaultMessageListenerConcurrently(delayLevelWhenNextConsume, skipWhenException));
        else
            consumer.setMessageListener(new DefaultMessageListenerOrderly(skipWhenException, maxReconsumeTimes, suspendCurrentQueueTimeMillis));

        log.info("success to subscribe nameServer:{}, topic:{}, tag:{}, group:{}", nameServer, topic, tag, group);
        return consumer;
    }

    private String getTag(String tag) {
        if (Func.isEmpty(tag) || tag.equals("*")) {
            tag = method.getDeclaringClass().getSimpleName() + "_" + method.getName();
        }
        return tag;
    }

    private class DefaultMessageListenerOrderly implements MessageListenerOrderly {

        private final boolean skipWhenException;
        private final int maxReconsumeTimes;
        private final long suspendCurrentQueueTimeMillis;


        public DefaultMessageListenerOrderly(boolean skipWhenException, int maxReconsumeTimes, long suspendCurrentQueueTimeMillis) {
            this.skipWhenException = skipWhenException;
            this.maxReconsumeTimes = maxReconsumeTimes;
            this.suspendCurrentQueueTimeMillis = suspendCurrentQueueTimeMillis;
        }

        @Override
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
            for (MessageExt messageExt : msgs) {
                String topic = messageExt.getTopic();
                String tag = messageExt.getTags();
                String msgId = messageExt.getMsgId();
                String msgKey = messageExt.getKeys();
                byte[] msgBody = messageExt.getBody();
                String traceId = messageExt.getUserProperty("tid");
                int reconsumeTimes = messageExt.getReconsumeTimes();
                String methodName = getMethod().getDeclaringClass().getSimpleName() + "." + getMethod().getName();
                try {
                    if (StringUtils.hasLength(traceId)) {
                        ThreadContext.put("traceId", traceId);
                        MDC.put("traceId", traceId);
                    }

                    Object[] methodParameters = getMethodParameters(msgBody);

                    long now = System.currentTimeMillis();
                    getMethod().invoke(getBean(), methodParameters);
                    long costTime = System.currentTimeMillis() - now;

                    log.info("MQ消费成功:[Method:{}], [Topic:{}], [Tag:{}], [Id:{}], [Key:{}], [消息体:{}], [耗时:{}毫秒]{}", methodName, topic, tag, msgId, msgKey, new String(msgBody, StandardCharsets.UTF_8), costTime, StringUtils.hasText(traceId) ? "[TraceId:" + traceId + "]" : "");
                } catch (Exception e) {
                    Throwable t = Exceptions.unwrap(e);
                    log.error("MQ消费失败:[Method:{}], [Topic:{}], [Tag:{}], [Id:{}], [Key:{}], [消息体:{}], [异常:{}], [重试:{}次]{}", methodName, topic, tag, msgId, msgKey, new String(msgBody, StandardCharsets.UTF_8), t.getMessage(), reconsumeTimes, StringUtils.hasText(traceId) ? "[TraceId:" + traceId + "]" : "", t);

                    ConsumeOrderlyStatus consumeOrderlyStatus;
                    if (skipWhenException || reconsumeTimes >= maxReconsumeTimes) {
                        consumeOrderlyStatus = ConsumeOrderlyStatus.SUCCESS;
                    } else {
                        context.setSuspendCurrentQueueTimeMillis(this.suspendCurrentQueueTimeMillis);
                        consumeOrderlyStatus = ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                    }
                    return consumeOrderlyStatus;
                } finally {
                    if (StringUtils.hasLength(traceId)) {
                        ThreadContext.clearAll();
                        MDC.clear();
                    }
                }
            }

            return ConsumeOrderlyStatus.SUCCESS;
        }
    }

    private class DefaultMessageListenerConcurrently implements MessageListenerConcurrently {

        private boolean skipWhenException = false;
        private Integer delayLevelWhenNextConsume = 1;

        public DefaultMessageListenerConcurrently() {
        }

        public DefaultMessageListenerConcurrently(Integer delayLevelWhenNextConsume, boolean skipWhenException) {
            this.delayLevelWhenNextConsume = delayLevelWhenNextConsume;
            this.skipWhenException = skipWhenException;
        }

        @SuppressWarnings("unchecked")
        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
            for (MessageExt messageExt : msgs) {
                String topic = messageExt.getTopic();
                String tag = messageExt.getTags();
                String msgId = messageExt.getMsgId();
                String msgKey = messageExt.getKeys();
                byte[] msgBody = messageExt.getBody();
                String traceId = messageExt.getUserProperty("tid");
                int reconsumeTimes = messageExt.getReconsumeTimes();
                String methodName = getMethod().getDeclaringClass().getSimpleName() + "." + getMethod().getName();
                try {
                    if (StringUtils.hasLength(traceId)) {
                        ThreadContext.put("traceId", traceId);
                        MDC.put("traceId", traceId);
                    }

                    Object[] methodParameters = getMethodParameters(msgBody);

                    long now = System.currentTimeMillis();
                    getMethod().invoke(getBean(), methodParameters);
                    long costTime = System.currentTimeMillis() - now;

                    log.info("MQ消费成功:[Method:{}], [Topic:{}], [Tag:{}], [Id:{}], [Key:{}], [消息体:{}], [耗时:{}毫秒]{}", methodName, topic, tag, msgId, msgKey, new String(msgBody, StandardCharsets.UTF_8), costTime, StringUtils.hasText(traceId) ? "[TraceId:" + traceId + "]" : "");
                } catch (Exception e) {
                    Throwable t = Exceptions.unwrap(e);
                    log.error("MQ消费失败:[Method:{}], [Topic:{}], [Tag:{}], [Id:{}], [Key:{}], [消息体:{}], [异常:{}], [重试:{}次]{}", methodName, topic, tag, msgId, msgKey, new String(msgBody, StandardCharsets.UTF_8), t.getMessage(), reconsumeTimes, StringUtils.hasText(traceId) ? "[TraceId:" + traceId + "]" : "", t);

                    if (skipWhenException) {
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }

                    context.setDelayLevelWhenNextConsume(delayLevelWhenNextConsume);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                } finally {
                    if (StringUtils.hasLength(traceId)) {
                        ThreadContext.clearAll();
                        MDC.clear();
                    }
                }
            }

            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }

    private Object[] getMethodParameters(byte[] body) {
        String bodyAsStr = new String(body, StandardCharsets.UTF_8);
        Map<String, String> deserialize = JsonUtil.parse(bodyAsStr, new TypeReference<>() {
        });
        Object[] params = new Object[getMethod().getParameterCount()];
        if (Func.isEmpty(deserialize)) {
            return params;
        }

        // 根据类型对每个参数进行反序列化
        for (int i = 0; i < getMethod().getParameterCount(); i++) {
            String o = deserialize.get(String.valueOf(i));
            if (Func.isEmpty(o)) {
                params[i] = null;
            } else {
                params[i] = JsonUtil.parse(o, getMethod().getParameterTypes()[i]);
            }
        }
        return params;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 构建 DefaultMQPushConsumer
        this.consumer = createConsumer();
    }

    @Override
    public void stop(Runnable runnable) {
        stop();
    }

    @Override
    public void start() {
        if (this.running) {
            return;
        }

        doStart();
        this.running = true;
    }

    @Override
    public void stop() {
        this.running = false;
        doShutdown();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return 0;
    }

}
