package com.lvwj.halo.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lvwj.halo.common.models.entity.IEntity;
import com.lvwj.halo.core.track.TrackManager;

import java.io.Serializable;
import java.util.List;

/**
 * 变更追踪
 *
 * @author lvweijie
 * @date 2023/11/9 17:26
 */
public interface TrackService<T extends IEntity<?>> extends JoinService<T> {

    /**
     * 追踪查询
     *
     * @author lvweijie
     * @date 2023/11/9 17:26
     */
    T getByTrack(Serializable id);

    T getByTrack(Wrapper<T> wrapper);

    List<T> listByTrack(List<Serializable> ids);

    List<T> listByTrack(Wrapper<T> wrapper);

    default List<T> listByTrack() {
        return listByTrack(Wrappers.emptyWrapper());
    }

    /**
     * 追踪保存 (创建 或 更新)
     *
     * @author lvweijie
     * @date 2023/11/9 17:26
     */
    void saveByTrack(T t);

    /**
     * 追踪保存 (创建 或 更新)
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
