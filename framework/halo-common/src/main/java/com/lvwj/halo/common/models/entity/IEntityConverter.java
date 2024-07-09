package com.lvwj.halo.common.models.entity;

import java.util.List;

/**
 * 实体转换器：数据实体(PO) <=> 聚合根(AGG)
 *
 * @author lvweijie
 * @date 2023年11月07日 14:27
 */
public interface IEntityConverter<AGG extends IEntity<?>, PO extends IEntity<?>> {

    AGG fromPO(PO po);

    List<AGG> fromPOList(List<PO> po);

    PO toPO(AGG po);

    List<PO> toPOList(List<AGG> po);
}
