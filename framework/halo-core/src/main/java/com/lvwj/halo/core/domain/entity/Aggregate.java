package com.lvwj.halo.core.domain.entity;

import cn.hutool.extra.spring.SpringUtil;

import com.lvwj.halo.common.utils.CollectionUtil;
import com.lvwj.halo.core.domain.event.IDomainEvent;
import com.lvwj.halo.core.domain.event.IEventBus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.beans.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@ToString(callSuper = true, exclude = {"events"})
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Aggregate<ID extends Serializable> extends Entity<ID> implements IAggregate<ID>, IVersion {

  /**
   * 版本号
   */
  private Long version;

  /**
   * 当前聚合产生的领域事件
   */
  private transient List<IDomainEvent<?>> events;

  /**
   * 注册领域事件
   */
  public void registerEvent(IDomainEvent<?> event) {
    if (null == events) {
      this.events = new ArrayList<>();
    }
    this.events.add(event);
  }

  /**
   * 触发领域事件
   */
  @Override
  public void fireEvents() {
    if (CollectionUtil.isEmpty(this.events)) return;
    List<IDomainEvent<?>> eventsCopy = new ArrayList<>(this.events);
    this.cleanEvents();
    IEventBus eventBus = SpringUtil.getBean(IEventBus.class);
    eventBus.publishAll(eventsCopy);
  }

  @Override
  @Transient
  public List<IDomainEvent<?>> getEvents() {
    if (null == this.events) {
      return Collections.emptyList();
    }
    return Collections.unmodifiableList(events);
  }

  @Override
  public void cleanEvents() {
    events.clear();
  }
}
