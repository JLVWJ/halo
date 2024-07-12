package com.lvwj.halo.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.lvwj.halo.common.utils.Exceptions;
import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.rocketmq.annotation.RocketMQConsumer;
import com.lvwj.halo.rocketmq.annotation.TagHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.logging.log4j.ThreadContext;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.spring.support.RocketMQUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel.BROADCASTING;
import static org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel.CLUSTERING;

/**
 * RocketMQConsumerRegistry
 *
 * @author lvweijie
 * @date 2023年11月20日 17:19
 */
@Slf4j
public class RocketMQConsumerRegistry implements BeanPostProcessor, SmartLifecycle {

    private final Map<String, Map<String, MethodInvoker>> tagMethodMap = new HashMap<>();

    private final Map<String, DefaultMQPushConsumer> consumerMap = new HashMap<>();

    private boolean running;

    @Autowired
    private Environment environment;

    @Resource
    private RocketMQProperties rocketMQProperties;


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        RocketMQConsumer annotation = AnnotatedElementUtils.findMergedAnnotation(targetClass, RocketMQConsumer.class);
        if (null == annotation) {
            return bean;
        }
        boolean enable = BooleanUtils.toBoolean(resolve(annotation.enable()));
        if (enable) {
            initTagMethods(bean, beanName);
            initConsumer(annotation, beanName);
        }
        return bean;
    }

    private void initTagMethods(Object bean, String beanName) {
        Map<String, MethodInvoker> tagMethods = Maps.newHashMap();
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        List<Method> methods = MethodUtils.getMethodsListWithAnnotation(targetClass, TagHandler.class);
        methods.forEach(method -> {
            if (method.getParameterCount() != 1) {
                String msg = String.format("Bean[%s] Method[%s] must have only one param", beanName, method);
                throw new RuntimeException(msg);
            }
            TagHandler tagHandler = method.getAnnotation(TagHandler.class);
            String tag = tagHandler.value();
            if (!StringUtils.hasText(tag)) {
                String msg = String.format("Bean[%s] Method[%s] @TagHandler's value shouldn't be null or empty", beanName, method);
                throw new RuntimeException(msg);
            }
            String[] split = tag.split("\\|\\|");
            for (String s : split) {
                if (!StringUtils.hasText(s)) {
                    String msg = String.format("Bean[%s] Method[%s] @TagHandler's value[%s] format error", beanName, method, tag);
                    throw new RuntimeException(msg);
                }
                if (tagMethods.containsKey(s)) {
                    String msg = String.format("Bean[%s] Method[%s] Tag[%s] is duplicate", beanName, method, s);
                    throw new RuntimeException(msg);
                }
                tagMethods.put(s, new MethodInvoker(bean, method));
            }
        });
        if (!tagMethods.isEmpty()) {
            tagMethodMap.put(beanName, tagMethods);
        }
    }

    private void initConsumer(RocketMQConsumer annotation, String beanName) {
        String nameServer = resolve(annotation.nameServer());
        nameServer = StringUtils.hasText(nameServer) ? nameServer : rocketMQProperties.getNameServer();
        String namespace = resolve(annotation.namespace());
        String topic = resolve(annotation.topic());
        String consumerGroup = resolve(annotation.consumerGroup());

        Assert.notNull(consumerGroup, "@RocketMQConsumer 'consumerGroup' is required");
        Assert.notNull(nameServer, "@RocketMQConsumer 'nameServer' is required");
        Assert.notNull(topic, "@RocketMQConsumer 'topic' is required");

        boolean enableMsgTrace = BooleanUtils.toBoolean(resolve(annotation.enableMsgTrace()));
        String customizedTraceTopic = resolve(annotation.customizedTraceTopic());
        int pullBatchSize = Integer.parseInt(resolve(annotation.pullBatchSize()));
        int consumeThreadNumber = Integer.parseInt(resolve(annotation.consumeThreadNumber()));
        int maxReconsumeTimes = Integer.parseInt(resolve(annotation.maxReconsumeTimes()));
        long consumeTimeout = Long.parseLong(resolve(annotation.consumeTimeout()));
        long awaitTerminationMillisWhenShutdown = Long.parseLong(resolve(annotation.awaitTerminationMillisWhenShutdown()));

        long suspendCurrentQueueTimeMillis = Long.parseLong(resolve(annotation.suspendCurrentQueueTimeMillis()));
        int delayLevelWhenNextConsume = Integer.parseInt(resolve(annotation.delayLevelWhenNextConsume()));

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup, enableMsgTrace, customizedTraceTopic);
        consumer.setNamesrvAddr(nameServer);
        if (StringUtils.hasText(namespace)) {
            consumer.setNamespace(namespace);
        }
        consumer.setInstanceName(annotation.instanceName());
        consumer.setPullBatchSize(pullBatchSize);
        consumer.setConsumeThreadMax(consumeThreadNumber);
        consumer.setConsumeThreadMin(consumeThreadNumber);
        consumer.setConsumeTimeout(consumeTimeout);
        consumer.setMaxReconsumeTimes(maxReconsumeTimes);
        consumer.setAwaitTerminationMillisWhenShutdown(awaitTerminationMillisWhenShutdown);
        switch (annotation.messageModel()) {
            case BROADCASTING:
                consumer.setMessageModel(MessageModel.BROADCASTING);
                break;
            case CLUSTERING:
                consumer.setMessageModel(CLUSTERING);
                break;
            default:
                throw new IllegalArgumentException("@RocketMQConsumer 'messageModel' was wrong");
        }
        switch (annotation.consumeMode()) {
            case ORDERLY:
                consumer.setMessageListener(new DefaultMessageListenerOrderly(suspendCurrentQueueTimeMillis, beanName, consumerGroup, annotation.messageModel()));
                break;
            case CONCURRENTLY:
                consumer.setMessageListener(new DefaultMessageListenerConcurrently(delayLevelWhenNextConsume, beanName, consumerGroup, annotation.messageModel()));
                break;
            default:
                throw new IllegalArgumentException("@RocketMQConsumer 'consumeMode' was wrong");
        }
        String tag = findTag(beanName);
        try {
            consumer.subscribe(topic, tag);
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        }
        log.info("success to subscribe nameServer:{}, topic:{}, tag:{}, consumerGroup:{}", nameServer, topic, tag, consumerGroup);
        consumerMap.put(beanName, consumer);
    }

    private String findTag(String beanName) {
        if (this.tagMethodMap.get(beanName).isEmpty()) {
            return "*";
        }
        return String.join("||", this.tagMethodMap.get(beanName).keySet());
    }

    private class DefaultMessageListenerOrderly implements MessageListenerOrderly {

        private final Logger log = LoggerFactory.getLogger(DefaultMessageListenerOrderly.class);

        private final long suspendCurrentQueueTimeMillis;
        private final String beanName;

        private final String consumerGroup;

        private final MessageModel messageModel;

        public DefaultMessageListenerOrderly(long suspendCurrentQueueTimeMillis, String beanName, String consumerGroup, MessageModel messageModel) {
            this.suspendCurrentQueueTimeMillis = suspendCurrentQueueTimeMillis;
            this.beanName = beanName;
            this.consumerGroup = consumerGroup;
            this.messageModel = messageModel;
        }

        private MethodInvoker getMethodInvoker(String tag) {
            tag = StringUtils.hasText(tag) ? tag : "*";
            MethodInvoker methodInvoker = tagMethodMap.get(beanName).get(tag);
            if (null == methodInvoker) {
                methodInvoker = tagMethodMap.get(beanName).get("*");
            }
            return methodInvoker;
        }

        @Override
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
            for (MessageExt messageExt : msgs) {
                String topic = messageExt.getTopic();
                String tag = messageExt.getTags();
                String msgId = messageExt.getMsgId();
                String msgKey = messageExt.getKeys();
                String msgPK = messageExt.getUserProperty("msgPK");
                String traceId = messageExt.getUserProperty("tid");
                //String operator = messageExt.getUserProperty("operator");
                String methodName = "";
                int reconsumeTimes = messageExt.getReconsumeTimes();
                Object payload = null;
                try {
                    if (StringUtils.hasLength(traceId)) {
                        ThreadContext.put("traceId", traceId);
                        MDC.put("traceId", traceId);
                    }
                    //取出消息头的操作人信息，放到上下文

                    //根据tag去找对应加了@TageHandler注解的方法
                    MethodInvoker methodInvoker = getMethodInvoker(tag);
                    if (methodInvoker == null) {
                        log.warn("Failed to find MethodInvoker for Tag {}", tag);
                        continue;
                    }
                    methodName = methodInvoker.methodName();

                    long now = System.currentTimeMillis();
                    //幂等判断：如该消息已被指定消费者组消费成功，需要过滤掉

                    //反序列化获取payload
                    payload = methodInvoker.deserialize(messageExt.getBody());
                    setEventIdIfNecessary(payload, Func.toLong(msgPK));
                    //取出消息体的操作人信息，放到上下文

                    //执行消费方法
                    methodInvoker.invoke(payload);
                    //保存消息消费成功记录

                    long costTime = System.currentTimeMillis() - now;
                    log.info("MQ消费成功: [方法]:{}, {}[MsgId]:{}, [MsgKey]:{}, [Topic]:{}, [Tag]:{}, [消息体]:{}, {}[耗时]:{}毫秒", methodName, StringUtils.hasText(msgPK) ? "[MsgPK]:" + msgPK + ", " : "", msgId, msgKey, topic, tag, JSON.toJSONString(payload), StringUtils.hasText(traceId) ? "[TraceId]:" + traceId + ", " : "", costTime);
                } catch (Exception e) {
                    Throwable t = Exceptions.unwrap(e);
                    log.warn("MQ消费失败: [方法]:{}, {}[MsgId]:{}, [MsgKey]:{}, [Topic]:{}, [Tag]:{}, [消息体]:{}, [异常]:{}, {}[重试]:{}次", methodName, StringUtils.hasText(msgPK) ? "[MsgPK]:" + msgPK + ", " : "", msgId, msgKey, topic, tag, JSON.toJSONString(payload), t.getMessage(), StringUtils.hasText(traceId) ? "[TraceId]:" + traceId + ", " : "", reconsumeTimes, t);
                    ConsumeOrderlyStatus consumeOrderlyStatus;
                    if (skipWhenException() || reconsumeTimes >= 5) { //重试5次 则返回消费成功 转人工处理
                        consumeOrderlyStatus = ConsumeOrderlyStatus.SUCCESS;
                    } else {
                        context.setSuspendCurrentQueueTimeMillis(this.suspendCurrentQueueTimeMillis);
                        consumeOrderlyStatus = ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                    }
                    //保存消息消费失败记录

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

        private final Logger log = LoggerFactory.getLogger(DefaultMessageListenerConcurrently.class);

        private final int delayLevelWhenNextConsume;
        private final String beanName;
        private final String consumerGroup;

        private final MessageModel messageModel;

        public DefaultMessageListenerConcurrently(int delayLevelWhenNextConsume, String beanName, String consumerGroup, MessageModel messageModel) {
            this.delayLevelWhenNextConsume = delayLevelWhenNextConsume;
            this.beanName = beanName;
            this.consumerGroup = consumerGroup;
            this.messageModel = messageModel;
        }

        private MethodInvoker getMethodInvoker(String tag) {
            tag = StringUtils.hasText(tag) ? tag : "*";
            MethodInvoker methodInvoker = tagMethodMap.get(beanName).get(tag);
            if (null == methodInvoker) {
                methodInvoker = tagMethodMap.get(beanName).get("*");
            }
            return methodInvoker;
        }

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
            for (MessageExt messageExt : msgs) {
                String topic = messageExt.getTopic();
                String tag = messageExt.getTags();
                String msgId = messageExt.getMsgId();
                String msgKey = messageExt.getKeys();
                String msgPK = messageExt.getUserProperty("msgPK");
                String traceId = messageExt.getUserProperty("tid");
                //String operator = messageExt.getUserProperty("operator");
                String methodName = "";
                int reconsumeTimes = messageExt.getReconsumeTimes();
                Object payload = null;
                try {
                    if (StringUtils.hasLength(traceId)) {
                        ThreadContext.put("traceId", traceId);
                        MDC.put("traceId", traceId);
                    }
                    //取出消息头的操作人信息，放到上下文

                    //根据tag去找对应加了@TageHandler注解的方法
                    MethodInvoker methodInvoker = getMethodInvoker(tag);
                    if (methodInvoker == null) {
                        log.warn("Failed to find MethodInvoker for Tag {}", tag);
                        continue;
                    }
                    methodName = methodInvoker.methodName();

                    long now = System.currentTimeMillis();
                    //幂等判断：如该消息已被指定消费者组消费成功，需要过滤掉

                    //反序列化获取payload
                    payload = methodInvoker.deserialize(messageExt.getBody());
                    setEventIdIfNecessary(payload, Func.toLong(msgPK));
                    //取出消息体的操作人信息，放到上下文

                    //执行消费方法
                    methodInvoker.invoke(payload);
                    //保存消息消费成功记录

                    long costTime = System.currentTimeMillis() - now;
                    log.info("MQ消费成功: [方法]:{}, {}[MsgId]:{}, [MsgKey]:{}, [Topic]:{}, [Tag]:{}, [消息体]:{}, {}[耗时]:{}毫秒", methodName, StringUtils.hasText(msgPK) ? "[MsgPK]:" + msgPK + ", " : "", msgId, msgKey, topic, tag, JSON.toJSONString(payload), StringUtils.hasText(traceId) ? "[TraceId]:" + traceId + ", " : "", costTime);
                } catch (Exception e) {
                    Throwable t = Exceptions.unwrap(e);
                    log.warn("MQ消费失败: [方法]:{}, {}[MsgId]:{}, [MsgKey]:{}, [Topic]:{}, [Tag]:{}, [消息体]:{}, [异常]:{}, {}[重试]:{}次", methodName, StringUtils.hasText(msgPK) ? "[MsgPK]:" + msgPK + ", " : "", msgId, msgKey, topic, tag, JSON.toJSONString(payload), t.getMessage(), StringUtils.hasText(traceId) ? "[TraceId]:" + traceId + ", " : "", reconsumeTimes, t);
                    ConsumeConcurrentlyStatus consumeConcurrentlyStatus;
                    if (skipWhenException()) {
                        consumeConcurrentlyStatus = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    } else {
                        context.setDelayLevelWhenNextConsume(delayLevelWhenNextConsume);
                        consumeConcurrentlyStatus = ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                    //保存消息消费失败记录

                    return consumeConcurrentlyStatus;
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

    private static void setEventIdIfNecessary(Object payload, Long eventId) {
        Field field = ReflectionUtils.findField(payload.getClass(), "eventId");
        if (null != field && null != eventId) {
            try {
                field.setAccessible(true);
                field.set(payload, eventId);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private String resolve(String value) {
        if (StringUtils.hasText(value)) {
            return this.environment.resolvePlaceholders(value);
        }
        return value;
    }

    private boolean skipWhenException() {
        return this.environment.getProperty("rocketmq.consumer.skipWhenException", Boolean.TYPE, false);
    }


    @Override
    public void start() {
        if (running) {
            return;
        }
        this.running = true;
        consumerMap.forEach((k, v) -> {
            try {
                v.start();
            } catch (MQClientException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void stop() {
        if (!running) {
            return;
        }
        this.running = false;
        consumerMap.forEach((k, v) -> v.shutdown());
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Slf4j
    static class MethodInvoker {
        private final Object bean;
        private final Method method;

        private final Class<?> declaringClass;
        private final Type paramType; //消息体类型


        MethodInvoker(Object bean, Method method) {
            this.bean = bean;
            this.method = method;
            this.declaringClass = method.getDeclaringClass();
            this.paramType = method.getGenericParameterTypes()[0];
        }

        public String methodName() {
            return this.declaringClass.getName() + "." + this.method.getName();
        }

        public void invoke(Object param) {
            if (null != param) {
                try {
                    this.method.invoke(bean, param);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw Exceptions.unchecked(e);
                }
            }
        }

        /**
         * 反序列化消息体
         */
        public Object deserialize(byte[] body) {
            String str = new String(body);
            //兼容旧消息体(BaseMqDto结构的)，只反回payload
            if (str.contains("payload")) {
                JSONObject jsonObject = JSON.parseObject(str);
                str = jsonObject.getString("payload");
            }
            return JSON.parseObject(str, this.paramType);
        }
    }
}
