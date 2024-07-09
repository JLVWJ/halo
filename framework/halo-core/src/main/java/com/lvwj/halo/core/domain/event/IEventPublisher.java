package com.lvwj.halo.core.domain.event;

import java.util.List;

/**
 * 事件发布者
 */
public interface IEventPublisher {

  default <E extends IEvent> void publish(E event) {
    publish(event, null);
  }

  <E extends IEvent> void publish(E event, String tag);

  default <E extends IEvent> void publishAll(List<E> events) {
    if (null == events || events.isEmpty()) return;
    events.forEach(this::publish);
  }
}
