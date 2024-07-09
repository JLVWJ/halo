package com.lvwj.halo.core.domain.event;


import com.lvwj.halo.core.domain.entity.IAggregate;

/**
 * 领域事件：用于上下文进程内部聚合之间通信
 */
public abstract class DomainEvent<A extends IAggregate<?>> implements IDomainEvent<A> {

  private final A agg;

  public DomainEvent(A agg) {
    this.agg = agg;
  }

  @Override
  public A agg() {
    return this.agg;
  }
}
