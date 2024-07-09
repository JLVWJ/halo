package com.lvwj.halo.core.track.impl;

import com.lvwj.halo.common.models.entity.IEntity;
import com.lvwj.halo.core.track.TrackContext;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lvweijie
 * @date 2023年11月03日 15:31
 */
public class MapTrackContext<E extends IEntity<?>>  implements TrackContext<E> {

    private final Map<Serializable, E> snapshots;

    public MapTrackContext() {
        this.snapshots = new HashMap<>();
    }


    @Override
    public void add(Serializable id, E po) {
        if (null == id) {
            return;
        }
        E snapshot = SerializationUtils.clone(po);
        if (null != snapshot) {
            snapshots.put(id, snapshot);
        }
    }

    @Override
    public E find(Serializable id) {
        if (null == id) {
            return null;
        }
        return snapshots.get(id);
    }

    @Override
    public void remove(Serializable id) {
        if (null == id) {
            return;
        }
        snapshots.remove(id);
    }

    @Override
    public List<E> findList(List<Serializable> ids) {
        if (null == ids || ids.isEmpty()) {
            return null;
        }
        List<E> list = new ArrayList<>(ids.size());
        for (Serializable id : ids) {
            E e = find(id);
            if (null != e) {
                list.add(e);
            }
        }
        return list;
    }
}
