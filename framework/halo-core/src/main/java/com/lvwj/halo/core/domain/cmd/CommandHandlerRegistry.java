package com.lvwj.halo.core.domain.cmd;

import com.lvwj.halo.common.utils.ClassUtil;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

/**
 * @author lvweijie
 * @date 2024年04月01日 13:51
 */
@Slf4j
@Component
public class CommandHandlerRegistry implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        CommandHandler annotation = AnnotatedElementUtils.findMergedAnnotation(targetClass, CommandHandler.class);
        List<Method> methodList = MethodUtils.getMethodsListWithAnnotation(targetClass, CommandHandler.class);
        if (null != annotation || !CollectionUtils.isEmpty(methodList)) {
            //遍历Public方法
            for (Method method : targetClass.getMethods()) {
                Parameter[] parameters = method.getParameters();
                //判断方法是否合法
                if (method.getModifiers() == Modifier.ABSTRACT
                        || method.getModifiers() == Modifier.PROTECTED
                        || method.getModifiers() == Modifier.PRIVATE
                        || method.getModifiers() == Modifier.NATIVE
                        || parameters.length != 1
                        || ClassUtil.isSimpleType(parameters[0].getType())
                        || !parameters[0].getType().getSimpleName().endsWith("Cmd")) {
                    if (methodList.contains(method)) {
                        throw new RuntimeException(String.format("CommandHandler[%s] method[%s] param[%s] is illegal", targetClass.getSimpleName(), method.getName(), Arrays.toString(parameters)));
                    }
                    continue;
                }
                if (null != annotation && !methodList.contains(method)) {
                    methodList.add(method);
                }
            }
            registerToCommandBus(bean, methodList);
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    private void registerToCommandBus(Object bean, List<Method> methodList) {
        if (CollectionUtils.isEmpty(methodList)) {
            return;
        }
        methodList.forEach(m -> CommandBus.register(bean, m));
    }
}
