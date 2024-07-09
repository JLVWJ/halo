package com.lvwj.halo.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lvwj.halo.common.models.entity.IEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 关联查询
 *
 * @author lvweijie
 * @date 2023/11/9 17:26
 */
public interface JoinService<T extends IEntity<?>> extends BaseService<T> {

    T getByJoin(Serializable id);

    T getByJoin(Wrapper<T> wrapper);

    List<T> listByJoin(List<Serializable> ids);

    List<T> listByJoin(Wrapper<T> wrapper);

    default List<T> listByJoin() {
        return listByJoin(Wrappers.emptyWrapper());
    }

    <E extends IPage<T>> E pageByJoin(E page, Wrapper<T> wrapper);

    default <E extends IPage<T>> E pageByJoin(E page) {
        return pageByJoin(page, Wrappers.emptyWrapper());
    }
}
