package com.lvwj.halo.core.domain.event;

import com.lvwj.halo.common.utils.Func;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事件总线
 */
public class EventBus implements IEventBus {

  public static final String GLOBAL_SUBSCRIBER = "global_subscriber";

  private final Map<String, List<RegisterItem>> registerItemMap = new ConcurrentHashMap<>();

  @Override
  public <E extends IEvent> void publish(E event, String tag) {
    if (null == event) return;
    publishLocalEvent(event); //1.发布本地事件：领域事件(实现IDomainEvent<?>) or SpringEvent(实现IEvent)
    IEvent integrationEvent = event;
    if (event instanceof IDomainEvent<?>) {
      integrationEvent = ((IDomainEvent<?>) event).toIntegrationEvent();
      if (null == integrationEvent) {
        return;
      }
    }
    //2.发布集成事件(实现IIntegrationEvent)
    publishIntegrationEvent(integrationEvent, tag);
  }

  /**
   * 发布领域事件
   */
  private <E extends IEvent> void publishLocalEvent(E event) {
    List<RegisterItem> items = registerItemMap.get(GLOBAL_SUBSCRIBER);
    if (Func.isNotEmpty(items)) {
      for (RegisterItem item : items) {
        item.handEvent(event);
      }
    }
  }

  /**
   * 发布集成事件
   */
  private <E extends IEvent> void publishIntegrationEvent(E event, String tag) {
    if (!(event instanceof IIntegrationEvent integrationEvent)) {
      return;
    }
    tag = Func.isNotBlank(tag) ? tag : integrationEvent.tag();
    List<RegisterItem> items = registerItemMap.get(integrationEvent.getClass().getName() + " | " + tag);
    if (Func.isNotEmpty(items)) {
      for (RegisterItem item : items) {
        item.handEvent(event);
      }
    }
  }

  @Override
  public <E extends IEvent> void register(String subscriber, IEventExecutor executor, IEventHandler<E> handler) {
    Assert.hasText(subscriber, "[DomainEventBus] register => subscriber shouldn't be null or empty!");
    List<RegisterItem> items = registerItemMap.computeIfAbsent(subscriber, k -> new ArrayList<>());
    items.add(new RegisterItem(executor, handler));
  }

  @Override
  public void unregister(String subscriber) {
    Assert.hasText(subscriber, "[DomainEventBus] unregister => subscriber shouldn't be null or empty!");
    registerItemMap.remove(subscriber);
  }

  @Data
  private static class RegisterItem {

    private final IEventExecutor executor;
    private final IEventHandler eventHandler;

    public <E extends IEvent> void handEvent(E event) {
      executor.submit(eventHandler, event);
    }
  }
}
