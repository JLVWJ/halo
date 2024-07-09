package com.lvwj.halo.statemachine;

import com.lvwj.halo.common.models.IContext;

import java.util.List;

/**
 * 状态处理器
 *
 * @author lvweijie
 * @date 2023年11月17日 17:41
 */
public interface StateProcessor<S, E, C extends IContext> {

    String machineId();

    List<StateTransition<S, E>> transitions();

    /**
     * 状态迁移逻辑执行前业务检查
     *
     * @param context 上下文
     */
    boolean isSatisfied(C context);

    /**
     * 状态机迁移逻辑执行
     *
     * @param context 上下文
     */
    void process(S from, S to, E event, C context);

    /**
     * 状态机迁移前置处理
     *
     * @param context 上下文
     */
    default void processBefore(S from, S to, E event, C context) {

    }

    /**
     * 状态机迁移后置处理
     *
     * @param context 上下文
     */
    default void processAfter(S from, S to, E event, C context) {

    }

    /**
     * 状态机迁移异常处理
     *
     * @param context 上下文
     */
    default void processException(S from, S to, E event, C context, Exception e) {

    }


    /**
     * 状态机迁移最终处理
     *
     * @param context 上下文
     */
    default void processFinally(S from, S to, E event, C context) {

    }
}
