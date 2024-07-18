package com.lvwj.halo.core.track;

import com.lvwj.halo.common.models.entity.IEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 变更追踪服务
 *
 * @author lvweijie
 * @date 2024年06月10日 18:56
 */
public interface TrackService<T extends IEntity<?>> {

    default T getOneById(Serializable id) {
        return getOneById(id, true);
    }

    T getOneById(Serializable id, boolean join);

    default List<T> getListByIds(List<Serializable> ids) {
        return getListByIds(ids, true);
    }

    List<T> getListByIds(List<Serializable> ids, boolean join);

    default List<T> getAllList() {
        return getAllList(true);
    }

    List<T> getAllList(boolean join);

    /**
     * 追踪保存
     *
     * @author lvweijie
     * @date 2023/11/9 17:26
     */
    void saveByTrack(T t);

    /**
     * 追踪保存
     *
     * @author lvweijie
     * @date 2023/11/9 17:26
     */
    void saveByTrack(List<T> list);

    /**
     * 追踪移除
     *
     * @author lvweijie
     * @date 2023/11/9 17:26
     */
    void removeByTrack(T t);

    /**
     * 追踪移除
     *
     * @author lvweijie
     * @date 2023/11/9 17:26
     */
    void removeByTrack(List<T> list);

    /**
     * 变更追踪管理器
     *
     * @author lvweijie
     * @date 2023/11/8 17:04
     */
    TrackManager<T> getTrackManager();
}
