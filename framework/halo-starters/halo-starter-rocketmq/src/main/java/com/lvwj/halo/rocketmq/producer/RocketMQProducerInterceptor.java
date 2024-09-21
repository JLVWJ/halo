package com.lvwj.halo.rocketmq.producer;


import com.google.common.collect.Maps;
import com.lvwj.halo.common.constants.NumberConstant;
import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.common.utils.JsonUtil;
import com.lvwj.halo.common.utils.TransactionUtil;
import com.lvwj.halo.core.domain.event.IntegrationEvent;
import com.lvwj.halo.core.threadpool.ThreadPoolCache;
import com.lvwj.halo.rocketmq.annotation.MessageMode;
import com.lvwj.halo.rocketmq.annotation.RocketMQProducer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.ThreadContext;
import org.apache.rocketmq.client.impl.CommunicationMode;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.env.Environment;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;

/**
 * AOP拦截器
 *
 * @author lvweijie
 * @date 2023年11月20日 18:37
 */
@Slf4j
public class RocketMQProducerInterceptor implements MethodInterceptor {

    private final Map<Method, InvokeCacheItem> invokeCache = Maps.newConcurrentMap();

    private final ExpressionParser expressionParser = new SpelExpressionParser();

    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private final Executor executor = ThreadPoolCache.getCustomizeThreadPool("RocketMQProducer", 2, 4, 5000);

    @Autowired
    private Environment environment;

    @Autowired
    private RocketMQProducerHelper producerHelper;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Object[] arguments = invocation.getArguments();
        // 先执行方法
        Object result = invocation.proceed();
        // 从方法中解析注解信息
        InvokeCacheItem invokeItem = this.invokeCache.computeIfAbsent(method, this::parseMethod);
        if (invokeItem.isEnable()) {
            ThreadContext.putIfNull("traceId", TraceContext.traceId());
            TransactionUtil.afterCommit(() -> sendMQ(invokeItem, arguments, result));
        }
        return result;
    }

    private void sendMQ(InvokeCacheItem invokeItem, Object[] arguments, Object result) {
        IntegrationEvent event = (IntegrationEvent) arguments[0];
        String msgKey = invokeItem.getKey(arguments, result);
        String msgBody = JsonUtil.toJson(event);
        Integer delayLevel = invokeItem.getDelayLevel(arguments);
        //顺序模式，同步发MQ; 普通模式，异步发MQ
        if (invokeItem.getMessageMode().equals(MessageMode.ORDER)) {
            producerHelper.apply(event.getEventId(), msgKey, invokeItem.getTopic(), invokeItem.getTag(), msgBody, delayLevel,
                    invokeItem.getMessageMode(), invokeItem.getCommunicationMode(), invokeItem.getTimeout(), invokeItem.bodyWithHeader, event.isStore());
        } else {
            executor.execute(() -> producerHelper.apply(event.getEventId(), msgKey, invokeItem.getTopic(), invokeItem.getTag(), msgBody, delayLevel,
                    invokeItem.getMessageMode(), invokeItem.getCommunicationMode(), invokeItem.getTimeout(), invokeItem.bodyWithHeader, event.isStore()));
        }
    }

    private InvokeCacheItem parseMethod(Method method) {
        RocketMQProducer producer = method.getAnnotation(RocketMQProducer.class);

        boolean enable = BooleanUtils.toBoolean(this.resolve(producer.enable()));
        String topic = this.resolve(producer.topic());
        String tag = this.resolve(producer.tag());

        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        Expression keyExp = null;
        if (StringUtils.hasText(producer.key())) {
            keyExp = expressionParser.parseExpression(producer.key());
        }
        Expression delayLevelExp = null;
        if (StringUtils.hasText(producer.delayLevel())) {
            delayLevelExp = expressionParser.parseExpression(producer.delayLevel());
        }

        return new InvokeCacheItem(enable, topic, tag, producer.key(), delayLevelExp, producer.msg(),
                producer.communicationMode(), producer.messageMode(),
                parameterNames, keyExp, producer.timeout(), producer.bodyWithHeader());
    }

    /**
     * 解析表达式，获取配置信息
     */
    protected String resolve(String value) {
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
        private final String key;
        private final Expression delayLevelExp;
        private final Class<? extends IntegrationEvent> eventClass;
        private final CommunicationMode communicationMode;
        private final MessageMode messageMode;
        private final String[] parameterNames;
        private final Expression keyExp;
        private final long timeout;
        private final boolean bodyWithHeader;

        public String getKey(Object[] arguments, Object result) {
            if (!StringUtils.hasLength(this.key)) return null;
            EvaluationContext evaluationContext = new StandardEvaluationContext();
            if (Func.isNotEmpty(result)) {
                evaluationContext.setVariable("_ret", result);
            }
            for (int i = 0; i < parameterNames.length; i++) {
                evaluationContext.setVariable(parameterNames[i], arguments[i]);
            }
            return keyExp.getValue(evaluationContext, String.class);
        }

        public Integer getDelayLevel(Object[] arguments) {
            if(null == delayLevelExp) return NumberConstant.INT_NEG_ONE;
            EvaluationContext evaluationContext = new StandardEvaluationContext();
            for (int i = 0; i < parameterNames.length; i++) {
                evaluationContext.setVariable(parameterNames[i], arguments[i]);
            }
            return Optional.ofNullable(delayLevelExp.getValue(evaluationContext, Integer.class)).orElse(NumberConstant.INT_NEG_ONE);
        }
    }
}
