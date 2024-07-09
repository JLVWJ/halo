package com.lvwj.halo.core.domain.event;

/**
 * 领域事件处理器
 */
public interface IEventHandler<E extends IEvent> {
    void handle(E event) throws Throwable;
}
