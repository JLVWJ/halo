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
public abstract class JoinServiceImpl<M extends CustomMapper<T>, T extends IEntity<?>> extends BaseServiceImpl<M,T> implements JoinService<T> {

    /**
     * 当前数据实体类有关联实体
     */
    protected Boolean entityClassIsJoin() {
        return !EntityHolder.getEntityJoinFields(getEntityClass()).isEmpty();
    }

    @Override
    public T getOneById(Serializable id, boolean join) {
        if (null == id) return null;
        T t = this.getById(id);
        if (join) {
            joinEntity(Collections.singletonList(t));
        }
        return t;
    }


    @Override
    public List<T> getListByIds(List<Serializable> ids, boolean join) {
        if (CollectionUtils.isEmpty(ids)) return null;
        List<T> list = listByIds(ids);
        if (join) {
            joinEntity(list);
        }
        return list;
    }

    @Override
    public List<T> getAllList(boolean join) {
        List<T> list = list();
        if (join) {
            joinEntity(list);
        }
        return list;
    }

    @Override
    public T getOneByCondition(Wrapper<T> wrapper, boolean join) {
        if (null == wrapper) return null;
        if (wrapper instanceof QueryWrapper) {
            ((QueryWrapper<T>) wrapper).last(" limit 1");
        }
        if (wrapper instanceof LambdaQueryWrapper) {
            ((LambdaQueryWrapper<T>) wrapper).last(" limit 1");
        }
        T t = this.getOne(wrapper);
        if (join) {
            joinEntity(Collections.singletonList(t));
        }
        return t;
    }

    @Override
    public List<T> getListByCondition(Wrapper<T> wrapper, boolean join) {
        List<T> list = null != wrapper ? super.list(wrapper) : super.list();
        if (join) {
            joinEntity(list);
        }
        return list;
    }

    @Override
    public <E extends IPage<T>> E pageByCondition(E page, Wrapper<T> wrapper, boolean join) {
        E result = null != wrapper ? super.page(page, wrapper) : super.page(page);
        List<T> list = result.getRecords();
        if (join) {
            joinEntity(list);
        }
        return result;
    }

    private void joinEntity(List<T> list) {
        if (entityClassIsJoin() && !CollectionUtils.isEmpty(list) && null != list.get(0)) {
            List<IEntity<?>> entities = new ArrayList<>(list.size());
            entities.addAll(list);
            //循环加载关联数据
            EntityHolder.joinEntity(getEntityClass(), entities);
        }
    }
}
