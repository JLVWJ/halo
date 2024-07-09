package com.lvwj.halo.core.track;

import com.lvwj.halo.common.models.entity.IEntity;

import java.io.Serializable;
import java.util.List;

public interface TrackContext<E extends IEntity<?>> {

    void add(Serializable id, E po);

    E find(Serializable id);

    void remove(Serializable id);

    List<E> findList(List<Serializable> ids);
}
