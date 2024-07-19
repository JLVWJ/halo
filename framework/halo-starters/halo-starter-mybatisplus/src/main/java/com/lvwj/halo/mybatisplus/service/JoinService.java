package com.lvwj.halo.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lvwj.halo.common.models.entity.IEntity;
import com.lvwj.halo.core.track.TrackService;

import java.util.List;

/**
 * 关联查询
 *
 * @author lvweijie
 * @date 2023/11/9 17:26
 */
public interface JoinService<T extends IEntity<?>> extends TrackService<T> {

    T getOneByCondition(Wrapper<T> wrapper, boolean join);

    default T getOneByCondition(Wrapper<T> wrapper) {
        return getOneByCondition(wrapper, true);
    }

    List<T> getListByCondition(Wrapper<T> wrapper, boolean join);

    default List<T> getListByCondition(Wrapper<T> wrapper) {
        return getListByCondition(wrapper, true);
    }

    <E extends IPage<T>> E pageByCondition(E page, Wrapper<T> wrapper, boolean join);

    default <E extends IPage<T>> E pageByCondition(E page, boolean join) {
        return pageByCondition(page, Wrappers.emptyWrapper(), join);
    }

    default <E extends IPage<T>> E pageByCondition(E page) {
        return pageByCondition(page, true);
    }
}
