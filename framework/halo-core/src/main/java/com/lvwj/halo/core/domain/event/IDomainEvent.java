package com.lvwj.halo.core.domain.event;


import com.lvwj.halo.core.domain.entity.IAggregate;

import java.io.Serializable;

/**
 * 领域事件接口
 */
public interface IDomainEvent<A extends IAggregate<?>> extends IEvent {

  A agg();

  default Serializable aggId() {
    if (null != agg()) {
      return agg().getId();
    }
    return null;
  }

  /**
   * 转成集成事件，用于发布MQ
   *
   * @author lvweijie
   * @date 2023/11/20 18:32
   */
  default IIntegrationEvent toIntegrationEvent() {
    return null;
  }
}
