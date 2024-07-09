package com.lvwj.halo.common.models.entity;

import java.io.Serializable;

/**
 * 实体基接口(领域实体和数据实体需实现该接口)
 *
 * @author lvweijie
 */
public interface IEntity<ID extends Serializable> extends Serializable {

  ID getId();
}
