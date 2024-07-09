package com.lvwj.halo.core.domain.entity;

/**
 * 值对象
 *
 * @author lvweijie
 * @date 2024年06月10日 12:47
 */
public abstract class ValueObj<ID> implements IValueObj {

    /**
     * 值对象对应表主键(主要用于变更追踪)
     */
    private ID id;
}
