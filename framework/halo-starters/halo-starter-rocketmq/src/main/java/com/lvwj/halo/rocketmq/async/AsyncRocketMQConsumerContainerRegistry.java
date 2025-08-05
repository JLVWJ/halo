package com.lvwj.halo.rocketmq.async;

import com.google.common.collect.Lists;
import com.lvwj.halo.rocketmq.annotation.AsyncRocketMQ;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.env.Environment;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class AsyncRocketMQConsumerContainerRegistry implements BeanPostProcessor, SmartLifecycle {

    @Getter(AccessLevel.PROTECTED)
    private final List<AsyncRocketMQConsumerContainer> consumerContainers = Lists.newArrayList();

    private final Environment environment;

    private volatile boolean running;

    @SneakyThrows
    @Override
    public Object postProcessAfterInitialization(Object proxy, String beanName) throws BeansException {
        // 1. 获取 @AsyncRocketMQ 注解方法
        Class<?> targetCls = AopUtils.getTargetClass(proxy);
        List<Method> methodsListWithAnnotation = MethodUtils.getMethodsListWithAnnotation(targetCls, AsyncRocketMQ.class);

        // 2. 为每个 @AsyncRocketMQ 注解方法 注册 AsyncRocketMQConsumerContainer
        for (Method method : methodsListWithAnnotation) {
            if (method.isBridge()) {
                log.warn("method {} is bridge, break!", method);
                continue;
            }
            if (method.getModifiers() == Modifier.PRIVATE) {
                log.warn("method {} is private, break!", method);
                continue;
            }

            AsyncRocketMQ annotation = method.getAnnotation(AsyncRocketMQ.class);
            String consumerProfile = annotation.consumerProfile();
            if (!isActiveProfile(consumerProfile)) {
                log.warn("method {} consumerProfile {} is not active, break!", method, consumerProfile);
                continue;
            }

            Object bean = AopProxyUtils.getSingletonTarget(proxy);
            AsyncRocketMQConsumerContainer asyncConsumerContainer = new AsyncRocketMQConsumerContainer(this.environment, bean, method, annotation);
            asyncConsumerContainer.afterPropertiesSet();

            this.getConsumerContainers().add(asyncConsumerContainer);
        }

        return proxy;
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
        this.running = true;
        this.consumerContainers.forEach(AsyncRocketMQConsumerContainer::start);
    }

    @Override
    public void stop() {
        if (!this.running) {
            return;
        }

        this.running = false;
        this.consumerContainers.forEach(AsyncRocketMQConsumerContainer::stop);
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public int getPhase() {
        return 0;
    }

    /**
     * 是否为 激活 的 Profile
     */
    protected boolean isActiveProfile(String consumerProfile) {
        return StringUtils.isEmpty(consumerProfile) || Arrays.asList(this.environment.getActiveProfiles()).contains(consumerProfile);
    }
}
