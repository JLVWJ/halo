package com.lvwj.halo.core.domain.repository;

import com.lvwj.halo.common.models.entity.IEntity;
import com.lvwj.halo.common.models.entity.IEntityConverter;
import com.lvwj.halo.core.domain.entity.IAggregate;
import com.lvwj.halo.core.track.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * 变更追踪仓储实现
 *
 * @author lvwj
 * @date 2022-12-26 15:33
 */
public abstract class Repository<TS extends TrackService<PO>,
        DC extends IEntityConverter<AGG, PO>,
        AGG extends IAggregate<ID>,
        PO extends IEntity<?>,
        ID extends Serializable>
        implements IRepository<AGG, ID> {

  /**
   * 变更追踪
   */
  @Autowired
  protected TS dao;

  /**
   * 数据转换器
   */
  @Autowired
  protected DC converter;

  @Override
  public Optional<AGG> find(ID id, boolean track, boolean join) {
    PO po = dao.getOneById(id, join);
    if (track) {
      dao.getTrackManager().attach(po);
    }
    return Optional.ofNullable(converter.fromPO(po));
  }

  @Override
  public List<AGG> findList(List<ID> ids, boolean track, boolean join) {
    List<PO> poList = dao.getListByIds((List<Serializable>) ids, join);
    if (track) {
      dao.getTrackManager().attach(poList);
    }
    return converter.fromPOList(poList);
  }

  @Override
  public List<AGG> findAll(boolean track, boolean join) {
    List<PO> poList = dao.getAllList(join);
    if (track) {
      dao.getTrackManager().attach(poList);
    }
    return converter.fromPOList(poList);
  }

  @Override
  public void save(AGG entity) {
    if (null == entity) return;
    dao.saveByTrack(converter.toPO(entity));
    entity.fireEvents();
  }

  @Override
  public void save(List<AGG> entities) {
    if (CollectionUtils.isEmpty(entities)) return;
    dao.saveByTrack(converter.toPOList(entities));
    entities.forEach(IAggregate::fireEvents);
  }

  @Override
  public void remove(AGG entity) {
    if (null == entity) return;
    dao.removeByTrack(converter.toPO(entity));
    entity.fireEvents();
  }

  @Override
  public void remove(List<AGG> entities) {
    if (CollectionUtils.isEmpty(entities)) return;
    dao.removeByTrack(converter.toPOList(entities));
    entities.forEach(IAggregate::fireEvents);
  }
}
