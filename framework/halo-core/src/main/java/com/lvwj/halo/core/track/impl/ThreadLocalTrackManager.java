package com.lvwj.halo.core.track.impl;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.lvwj.halo.common.models.entity.IEntity;
import com.lvwj.halo.core.track.TrackContext;
import com.lvwj.halo.core.track.TrackManager;
import com.lvwj.halo.core.utils.DiffUtil;
import org.javers.core.Changes;
import org.javers.core.ChangesByObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基于ThreadLocal的变更追踪管理器
 *
 * @author lvweijie
 * @date 2023年11月03日 15:44
 */
public class ThreadLocalTrackManager<E extends IEntity<?>> implements TrackManager<E> {

    private static final List<IEntity<?>> EMPTY_LIST = new ArrayList<>();

    private final TransmittableThreadLocal<TrackContext<E>> context;

    public ThreadLocalTrackManager() {
        this.context = TransmittableThreadLocal.withInitial(MapTrackContext::new);
    }

    @Override
    public void attach(E e) {
        if (null == e) {
            return;
        }
        this.context.get().add(e.getId(), e);
    }

    @Override
    public void detach(E e) {
        if (null == e) {
            return;
        }
        this.context.get().remove(e.getId());
    }

    @Override
    public Changes changeDiff(E e) {
        if (null == e) {
            return null;
        }
        E old = this.context.get().find(e.getId());
        return DiffUtil.compare(old, e);
    }

    @Override
    public Changes changeDiff(List<E> list, Class<E> clazz) {
        List<Serializable> ids = (List<Serializable>) list.stream().map(IEntity::getId).collect(Collectors.toList());
        List<E> oldList = this.context.get().findList(ids);
        return DiffUtil.compareCollections(oldList, list, clazz);
    }

    @Override
    public List<ChangesByObject> changeDiffByObject(E e) {
        if (null == e) {
            return null;
        }
        E old = this.context.get().find(e.getId());
        return DiffUtil.compareGroupByObject(old, e);
    }

    @Override
    public List<ChangesByObject> changeDiffByObject(List<E> list, Class<E> clazz) {
        List<Serializable> ids = (List<Serializable>) list.stream().map(IEntity::getId).collect(Collectors.toList());
        List<E> oldList = this.context.get().findList(ids);
        return DiffUtil.compareGroupByObject(oldList, list, clazz);
    }

    @Override
    public Changes deleteDiff(E e) {
        if (null == e) {
            return null;
        }
        E old = this.context.get().find(e.getId());
        if (null == old) {
            old = e;
        }
        return DiffUtil.compare(old, null);
    }

    @Override
    public Changes deleteDiff(List<E> list, Class<E> clazz) {
        List<Serializable> ids = (List<Serializable>) list.stream().map(IEntity::getId).collect(Collectors.toList());
        List<E> oldList = this.context.get().findList(ids);
        if (null == oldList || oldList.size() != list.size()) {
            oldList = list;
        }
        return DiffUtil.compareCollections(oldList, (List<E>) EMPTY_LIST, clazz);
    }

    @Override
    public List<ChangesByObject> deleteDiffByObject(E e) {
        if (null == e) {
            return null;
        }
        E old = this.context.get().find(e.getId());
        if (null == old) {
            old = e;
        }
        return DiffUtil.compareGroupByObject(old, null);
    }

    @Override
    public List<ChangesByObject> deleteDiffByObject(List<E> list, Class<E> clazz) {
        List<Serializable> ids = (List<Serializable>) list.stream().map(IEntity::getId).collect(Collectors.toList());
        List<E> oldList = this.context.get().findList(ids);
        if (null == oldList || oldList.size() != list.size()) {
            oldList = list;
        }
        return DiffUtil.compareGroupByObject(oldList, (List<E>) EMPTY_LIST, clazz);
    }
}
