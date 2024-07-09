package com.lvwj.halo.core.domain.entity;


import com.lvwj.halo.common.models.entity.IEntity;
import com.lvwj.halo.core.domain.event.IDomainEvent;

import java.io.Serializable;
import java.util.List;

/**
 * 聚合根接口
 */
public interface IAggregate<ID extends Serializable> extends IEntity<ID> {

  void fireEvents();

  List<IDomainEvent<?>> getEvents();

  void cleanEvents();
}
