package com.lvwj.codegen.templateengine.config;

/**
 * 领域模型类型：聚合根、子实体、值对象
 *
 * @author lvweijie
 * @date 2024年11月08日 10:56
 */
public enum DomainModelType {
    aggregate, entity, valveObj;

    public static final String NAME = "DomainModelType";
}
