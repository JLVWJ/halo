package com.lvwj.halo.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lvwj.halo.common.models.entity.IEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * 自定义ORM服务接口
 *
 * @author lvwj
 * @date 2022-12-21 10:08
 */
public interface BaseService<T extends IEntity<?>> extends IService<T> {

  /**
   * 插入 如果表中已存在相同的记录，则忽略当前新数据
   *
   * @param entity entity
   * @return 是否成功
   */
  boolean saveIgnore(T entity);

  /**
   * 插入替换数据，需求表中有PrimaryKey，或者unique索引，如果数据库已经存在数据，则用新数据替换，如果没有数据效果则和insert into一样；
   *
   * @param entity entity
   * @return 是否成功
   */
  boolean saveReplace(T entity);

  /**
   * 插入（批量）,如果表中已存在相同的记录，则忽略当前新数据
   *
   * @param entityList 实体对象集合
   * @param batchSize  批次大小
   * @return 是否成功
   */
  boolean saveIgnoreBatch(Collection<T> entityList, int batchSize);

  /**
   * 插入（批量）,插入如果中已经存在相同的记录，则忽略当前新数据
   *
   * @param entityList 实体对象集合
   * @return 是否成功
   */
  @Transactional(rollbackFor = Exception.class)
  default boolean saveIgnoreBatch(Collection<T> entityList) {
    return saveIgnoreBatch(entityList, DEFAULT_BATCH_SIZE);
  }

  /**
   * 插入（批量）,表示插入替换数据，需求表中有PrimaryKey，或者unique索引，如果数据库已经存在数据，则用新数据替换，如果没有数据效果则和insert into一样；
   *
   * @param entityList 实体对象集合
   * @param batchSize  批次大小
   * @return 是否成功
   */
  boolean saveReplaceBatch(Collection<T> entityList, int batchSize);

  /**
   * 插入（批量）,表示插入替换数据，需求表中有PrimaryKey，或者unique索引，如果数据库已经存在数据，则用新数据替换，如果没有数据效果则和insert into一样；
   *
   * @param entityList 实体对象集合
   * @return 是否成功
   */
  @Transactional(rollbackFor = Exception.class)
  default boolean saveReplaceBatch(Collection<T> entityList) {
    return saveReplaceBatch(entityList, DEFAULT_BATCH_SIZE);
  }

  /**
   * 批量插入
   */
  boolean saveBatch(List<T> entityList);

  /**
   * 批量更新
   */
  boolean updateBatch(List<T> entityList);
}
