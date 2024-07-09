package com.lvwj.halo.mybatisplus.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lvwj.halo.common.models.entity.IEntity;
import com.lvwj.halo.mybatisplus.entity.EntityHolder;
import com.lvwj.halo.mybatisplus.mapper.CustomMapper;
import com.lvwj.halo.mybatisplus.service.JoinService;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 关联加载实现
 * 原理：基于@JoinEntity 自动关联加载子实体或值对象的数据
 *
 * @author lvweijie
 * @date 2023年11月03日 16:40
 */
public class JoinServiceImpl<M extends CustomMapper<T>, T extends IEntity<?>> extends BaseServiceImpl<M,T> implements JoinService<T> {

    /**
     * 当前数据实体类有关联实体
     */
    protected Boolean entityClassIsJoin() {
        return !EntityHolder.getEntityJoinFields(getEntityClass()).isEmpty();
    }

    @Override
    public T getByJoin(Serializable id) {
        if (null == id) {
            return null;
        }
        T t = this.getById(id);
        if (null == t) {
            return null;
        }
        //循环加载关联数据
        EntityHolder.joinEntity(getEntityClass(), Collections.singletonList(t));
        return t;
    }

    @Override
    public T getByJoin(Wrapper<T> wrapper) {
        if (wrapper instanceof QueryWrapper) {
            ((QueryWrapper<T>) wrapper).last(" limit 1");
        }
        if (wrapper instanceof LambdaQueryWrapper) {
            ((LambdaQueryWrapper<T>) wrapper).last(" limit 1");
        }
        T t = this.getOne(wrapper);
        if (null == t) {
            return null;
        }
        //循环加载关联数据
        EntityHolder.joinEntity(getEntityClass(), Collections.singletonList(t));
        return t;
    }

    @Override
    public List<T> listByJoin(List<Serializable> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        List<T> list = listByIds(ids);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        if (entityClassIsJoin()) {
            List<IEntity<?>> entities = new ArrayList<>(list.size());
            entities.addAll(list);
            //循环加载关联数据
            EntityHolder.joinEntity(getEntityClass(), entities);
        }
        return list;
    }

    @Override
    public List<T> listByJoin(Wrapper<T> wrapper) {
        List<T> list = super.list(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        if (entityClassIsJoin()) {
            List<IEntity<?>> entities = new ArrayList<>(list.size());
            entities.addAll(list);
            //循环加载关联数据
            EntityHolder.joinEntity(getEntityClass(), entities);
        }
        return list;
    }

    @Override
    public <E extends IPage<T>> E pageByJoin(E page, Wrapper<T> wrapper) {
        E result = super.page(page, wrapper);
        List<T> list = result.getRecords();
        if (!CollectionUtils.isEmpty(list) && entityClassIsJoin()) {
            List<IEntity<?>> entities = new ArrayList<>(list.size());
            entities.addAll(list);
            //循环加载关联数据
            EntityHolder.joinEntity(getEntityClass(), entities);
        }
        return result;
    }
}
