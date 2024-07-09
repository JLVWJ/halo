package com.lvwj.halo.core.domain.event;


/**
 * 事件处理注册器
 */
public interface IEventHandlerRegistry {

  default <E extends IEvent> void register(Class<E> eventCls, IEventHandler<E> handler) {
    register(eventCls.getName(), new IEventExecutor.SyncExecutor(), handler);
  }

  default <E extends IEvent> void register(Class<E> eventCls, IEventExecutor executor, IEventHandler<E> handler) {
    register(eventCls.getName(), executor, handler);
  }

  default <E extends IEvent> void registerAsync(Class<E> eventCls, IEventHandler<E> handler) {
    register(eventCls.getName(), new IEventExecutor.AsyncExecutor(), handler);
  }

  default <E extends IEvent> void registerAsync(String subscriber, IEventHandler<E> handler) {
    register(subscriber, new IEventExecutor.AsyncExecutor(), handler);
  }

  default <E extends IEvent> void register(String subscriber, IEventHandler<E> handler) {
    register(subscriber, new IEventExecutor.SyncExecutor(), handler);
  }

  <E extends IEvent> void register(String subscriber, IEventExecutor executor, IEventHandler<E> handler);

  void unregister(String subscriber);

  default <E extends IEvent> void unregister(Class<E> eventCls) {
    unregister(eventCls.getName());
  }
}
