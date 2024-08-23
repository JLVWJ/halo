package com.lvwj.halo.core.domain.cmd;


import com.lvwj.halo.common.utils.Exceptions;
import com.lvwj.halo.common.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 命令总线
 *
 * @author lvweijie
 * @date 2024年04月01日 14:02
 */
@Slf4j
public class CommandBus {

    private static final Map<Class<?>, CommandInvoker> map = new ConcurrentHashMap<>();

    public static void register(Object target, Method method) {
        Class<?> paramType = method.getParameterTypes()[0];
        if (map.containsKey(paramType)) {
            throw new RuntimeException(String.format("CommandHandler[%s] method[%s] param[%s] is repeated", target.getClass().getSimpleName(), method.getName(), paramType.getSimpleName()));
        }
        map.put(paramType, CommandInvoker.of(target, method));
    }

    public static Object exec(Object cmd) {
        if (null == cmd) {
            return null;
        }
        CommandInvoker invoker = map.get(cmd.getClass());
        if (null == invoker) {
            return null;
        }
        return invoker.invoke(cmd);
    }

    private static class CommandInvoker {

        public static CommandInvoker of(Object target, Method method) {
            return new CommandInvoker(target, method);
        }

        private CommandInvoker(Object target, Method method) {
            this.target = target;
            this.method = method;
            this.paramType = method.getParameterTypes()[0];
            this.returnType = method.getReturnType();
        }

        private final Object target;
        private final Method method;

        private final Class<?> paramType;

        private final Class<?> returnType;

        public Object invoke(Object param) {
            try {
                log.info(String.format("CommandBus exec %s: %s", paramType.getSimpleName(), JsonUtil.toJson(param)));
                return method.invoke(target, param);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw Exceptions.unchecked(e);
            }
        }
    }
}
