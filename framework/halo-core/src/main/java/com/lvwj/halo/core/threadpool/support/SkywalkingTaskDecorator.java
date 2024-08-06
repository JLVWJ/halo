package com.lvwj.halo.core.threadpool.support;

import org.apache.skywalking.apm.toolkit.trace.RunnableWrapper;
import org.springframework.core.task.TaskDecorator;

/**
 * 基于Skywalking的任务装饰器
 *
 * @author lvweijie
 * @date 2024年08月06日 21:50
 */
public class SkywalkingTaskDecorator implements TaskDecorator {

    public static final SkywalkingTaskDecorator INSTANCE = new SkywalkingTaskDecorator();

    @Override
    public Runnable decorate(Runnable runnable) {
        return RunnableWrapper.of(runnable);
    }
}
