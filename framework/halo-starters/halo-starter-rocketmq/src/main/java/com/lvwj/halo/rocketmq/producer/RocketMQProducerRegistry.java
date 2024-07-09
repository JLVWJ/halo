package com.lvwj.halo.rocketmq.producer;

import com.lvwj.halo.core.domain.event.IEventBus;
import com.lvwj.halo.rocketmq.annotation.RocketMQProducer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 把加了@RocketMQProducer的方法，注册到事件总线
 *
 * @author lvweijie
 * @date 2023年11月20日 17:19
 */
@Slf4j
public class RocketMQProducerRegistry implements BeanPostProcessor {

    @Resource
    private IEventBus eventBus;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        List<Method> methodList = MethodUtils.getMethodsListWithAnnotation(targetClass, RocketMQProducer.class);
        for (Method method : methodList) {
            if (method.isBridge()) {
                throw new RuntimeException(String.format("bean[%s] method[%s] with @RocketMQProducer: method is bridge", beanName, method));
            }
            //方法参数只能有一个
         /*   if (method.getParameterCount() != 1) {
                throw new RuntimeException(String.format("bean[%s] method[%s] parameter should only be one", beanName, method));
            }*/
            RocketMQProducer mqProducer = method.getAnnotation(RocketMQProducer.class);
            if (StringUtils.isBlank(mqProducer.topic())) {
                throw new RuntimeException(String.format("bean[%s] method[%s] with @RocketMQProducer: topic is blank", beanName, method));
            }
            if (StringUtils.isBlank(mqProducer.tag())) {
                throw new RuntimeException(String.format("bean[%s] method[%s] with @RocketMQProducer: tag is blank", beanName, method));
            }
            if (mqProducer.tag().split("\\|\\|").length > 1) {
                throw new RuntimeException(String.format("bean[%s] method[%s] with @RocketMQProducer: tag not support use '||'", beanName, method));
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (!parameterTypes[0].equals(mqProducer.msg())) {
                throw new RuntimeException(String.format("bean[%s] method[%s] with @RocketMQProducer: parameters[0] should be [%s]", beanName, method, mqProducer.msg().getName()));
            }
            if (!BooleanUtils.toBoolean(mqProducer.enable())) {
                log.info("bean[{}] method[{}] with @RocketMQProducer: enable isn't true!", beanName, method);
                continue;
            }
            //key = 消息类名 | Tag，主要是为了兼容多个tag共用一个消息类，这是非常不规范的做法。
            String key = mqProducer.msg().getName() + " | " + mqProducer.tag();
            eventBus.register(key, msg -> method.invoke(bean, msg));
        }
        return bean;
    }
}
