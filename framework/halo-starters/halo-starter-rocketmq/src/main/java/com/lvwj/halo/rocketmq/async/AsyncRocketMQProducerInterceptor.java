package com.lvwj.halo.rocketmq.async;

import com.google.common.collect.Maps;
import com.lvwj.halo.common.constants.NumberConstant;
import com.lvwj.halo.common.constants.SystemConstant;
import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.common.utils.JsonUtil;
import com.lvwj.halo.common.utils.StringPool;
import com.lvwj.halo.rocketmq.annotation.AsyncRocketMQ;
import com.lvwj.halo.rocketmq.annotation.MessageMode;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.logging.log4j.ThreadContext;
import org.apache.rocketmq.client.impl.CommunicationMode;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.env.Environment;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;

@Slf4j
public class AsyncRocketMQProducerInterceptor implements MethodInterceptor {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private Environment environment;

    @Resource
    private Executor asyncRocketMQThreadPool;

    private final Map<Method, InvokeCacheItem> invokeCache = Maps.newConcurrentMap();

    private final ExpressionParser expressionParser = new SpelExpressionParser();

    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Nullable
    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) {
        Method method = invocation.getMethod();
        InvokeCacheItem invokeCacheItem = invokeCache.computeIfAbsent(method, this::parseMethod);
        if (invokeCacheItem.isEnable()) {
            if (invokeCacheItem.messageMode.equals(MessageMode.ORDER)) {
                sendMessage(invocation, invokeCacheItem);
            } else {
                asyncRocketMQThreadPool.execute(() -> sendMessage(invocation, invokeCacheItem));
            }
        } else {
            asyncRocketMQThreadPool.execute(() -> {
                try {
                    invocation.proceed();
                } catch (Throwable e) {
                    log.error("[Method:{}]异步执行异常:{}", method.getDeclaringClass().getSimpleName() + "." + method.getName(), e.getMessage(), e);
                }
            });
        }
        return null;
    }

    private void sendMessage(MethodInvocation invocation, InvokeCacheItem invokeCacheItem) {
        Object[] arguments = invocation.getArguments();
        String topic = invokeCacheItem.getTopic();
        String tag = invokeCacheItem.getTag();
        String key = invokeCacheItem.getKey(arguments);
        Integer delayLevel = invokeCacheItem.getDelayLevel(arguments);
        CommunicationMode communicationMode = invokeCacheItem.getCommunicationMode();
        MessageMode messageMode = invokeCacheItem.getMessageMode();
        long timeout = 30000;
        // 获取Destination
        String destination = getDestination(topic, tag);
        // 获取消息体
        String body = getBody(arguments);
        // 获取Message
        Message<String> message = getMessage(null, body, key, tag, delayLevel);
        switch (communicationMode) {
            case SYNC:
                SendResult sendResult;
                if (messageMode == MessageMode.ORDER && StringUtils.hasText(key)) {
                    sendResult = this.rocketMQTemplate.syncSendOrderly(destination, message, key, timeout, delayLevel);
                } else {
                    sendResult = this.rocketMQTemplate.syncSend(destination, message, timeout, delayLevel);
                }
                String result = sendResult.getSendStatus().equals(SendStatus.SEND_OK) ? "成功" : "失败";
                log.info("[SYNC]MQ发送{}: [Topic:{}], [Tag:{}], [Id:{}], [Key:{}], [Msg:{}], [Result:{}]", result, topic, tag, sendResult.getMsgId(), key, JsonUtil.toJson(message), sendResult);
                break;
            case ASYNC:
                SendCallback sendCallback = new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        log.info("[ASYNC]MQ发送成功: [Topic:{}], [Tag:{}], [Id:{}], [Key:{}], [Msg:{}], [Result:{}]", topic, tag, sendResult.getMsgId(), key, JsonUtil.toJson(message), sendResult);
                    }

                    @Override
                    public void onException(Throwable e) {
                        log.error("[ASYNC]MQ发送失败: [Topic:{}], [Tag:{}], [Key:{}], [Msg:{}]", topic, tag, key, JsonUtil.toJson(message), e);
                    }
                };
                if (messageMode == MessageMode.ORDER && StringUtils.hasText(key)) {
                    this.rocketMQTemplate.asyncSendOrderly(destination, message, key, sendCallback, timeout, delayLevel);
                } else {
                    this.rocketMQTemplate.asyncSend(destination, message, sendCallback, timeout, delayLevel);
                }
                break;
            case ONEWAY:
                if (messageMode == MessageMode.ORDER && StringUtils.hasText(key)) {
                    this.rocketMQTemplate.sendOneWayOrderly(destination, message, key);
                } else {
                    this.rocketMQTemplate.sendOneWay(destination, message);
                }
                log.info("[ONEWAY]MQ发送成功: [Topic:{}], [Tag:{}], [Key:{}], [Msg:{}]", topic, tag, key, JsonUtil.toJson(message));
                break;
        }
    }

    private String getDestination(String topic, String tag) {
        if (StringUtils.hasText(tag)) {
            return topic + StringPool.COLON + tag;
        } else {
            return topic;
        }
    }

    private Message<String> getMessage(Long msgPK, String body, String key, String tag, int delayLevel) {
        MessageBuilder<String> builder = MessageBuilder.withPayload(body);
        String traceId = getTraceId();
        if (Func.isNotBlank(traceId)) {
            builder.setHeader(SystemConstant.TID, traceId);
        }
        if (null != msgPK && msgPK > 0) {
            builder.setHeader("msgPK", msgPK);
        }
        if (StringUtils.hasText(key)) {
            builder.setHeader(MessageConst.PROPERTY_KEYS, key);
        }
        if (StringUtils.hasText(tag)) {
            builder.setHeader(MessageConst.PROPERTY_TAGS, tag);
        }
        if (delayLevel > 0) {
            builder.setHeader(MessageConst.PROPERTY_DELAY_TIME_LEVEL, delayLevel);
        }
        return builder.build();
    }

    private String getBody(Object[] arguments) {
        Map<String, String> result = Maps.newHashMapWithExpectedSize(arguments.length);
        for (int i = 0; i < arguments.length; i++) {
            result.put(String.valueOf(i), JsonUtil.toJson(arguments[i]));
        }
        return JsonUtil.toJson(result);
    }

    private String getTraceId() {
        String traceId = ThreadContext.get(SystemConstant.TRACE_ID);
        if (Func.isBlank(traceId) || SystemConstant.DEFAULT_TID.equals(traceId)) {
            traceId = TraceContext.traceId();
        }
        if (SystemConstant.DEFAULT_TID.equals(traceId)) {
            traceId = StringPool.EMPTY;
        }
        return traceId;
    }

    private InvokeCacheItem parseMethod(Method method) {
        AsyncRocketMQ asyncRocketMQ = method.getAnnotation(AsyncRocketMQ.class);

        boolean enable = Func.toBoolean(this.resolve(asyncRocketMQ.enable()));
        String topic = this.resolve(asyncRocketMQ.topic());
        String tag = this.resolve(asyncRocketMQ.tag());
        if (Func.isEmpty(tag) || tag.equals("*")) {
            tag = method.getDeclaringClass().getSimpleName() + "_" + method.getName();
        }

        Expression keyExp = null;
        if (StringUtils.hasText(asyncRocketMQ.key())) {
            keyExp = expressionParser.parseExpression(asyncRocketMQ.key());
        }
        Expression delayLevelExp = null;
        if (StringUtils.hasText(asyncRocketMQ.delayLevel())) {
            delayLevelExp = expressionParser.parseExpression(asyncRocketMQ.delayLevel());
        }

        CommunicationMode communicationMode = asyncRocketMQ.communicationMode();
        MessageMode messageMode = asyncRocketMQ.messageMode();

        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);

        return new InvokeCacheItem(enable, topic, tag, keyExp, delayLevelExp, parameterNames, communicationMode, messageMode);
    }

    private String resolve(String value) {
        if (StringUtils.hasText(value)) {
            return this.environment.resolvePlaceholders(value);
        }
        return value;
    }

    @Data
    static class InvokeCacheItem {
        private final boolean enable;
        private final String topic;
        private final String tag;
        private final Expression keyExp;
        private final Expression delayLevelExp;
        private final String[] parameterNames;
        private final CommunicationMode communicationMode;
        private final MessageMode messageMode;

        public String getKey(Object[] arguments) {
            if (keyExp == null) return null;
            EvaluationContext evaluationContext = new StandardEvaluationContext();
            for (int i = 0; i < parameterNames.length; i++) {
                evaluationContext.setVariable(parameterNames[i], arguments[i]);
            }
            return keyExp.getValue(evaluationContext, String.class);
        }

        public Integer getDelayLevel(Object[] arguments) {
            if (null == delayLevelExp) return NumberConstant.INT_NEG_ONE;
            EvaluationContext evaluationContext = new StandardEvaluationContext();
            for (int i = 0; i < parameterNames.length; i++) {
                evaluationContext.setVariable(parameterNames[i], arguments[i]);
            }
            return Optional.ofNullable(delayLevelExp.getValue(evaluationContext, Integer.class)).orElse(NumberConstant.INT_NEG_ONE);
        }
    }
}
