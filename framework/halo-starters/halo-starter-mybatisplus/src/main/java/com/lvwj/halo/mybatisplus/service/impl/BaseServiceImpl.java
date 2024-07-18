package com.lvwj.halo.mybatisplus.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.google.common.collect.Lists;
import com.lvwj.halo.common.models.entity.IEntity;
import com.lvwj.halo.mybatisplus.entity.EntityHolder;
import com.lvwj.halo.mybatisplus.injector.CustomSqlMethod;
import com.lvwj.halo.mybatisplus.mapper.CustomMapper;
import com.lvwj.halo.mybatisplus.service.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * 自定义ORM服务实现
 *
 * @author lvwj
 * @date 2022-12-21 10:11
 */
public class BaseServiceImpl<M extends CustomMapper<T>, T extends IEntity<?>> extends ServiceImpl<M, T> implements BaseService<T> {

  /**
   * 当前数据实体类有关联实体
   */
  protected Boolean entityClassIsJoin() {
    return !EntityHolder.getEntityJoinFields(getEntityClass()).isEmpty();
  }

  @Override
  public boolean saveIgnore(T entity) {
    return SqlHelper.retBool(baseMapper.insertIgnore(entity));
  }

  @Override
  public boolean saveReplace(T entity) {
    return SqlHelper.retBool(baseMapper.replace(entity));
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean saveIgnoreBatch(Collection<T> entityList, int batchSize) {
    return saveBatch(entityList, batchSize, CustomSqlMethod.INSERT_IGNORE_ONE);
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean saveReplaceBatch(Collection<T> entityList, int batchSize) {
    return saveBatch(entityList, batchSize, CustomSqlMethod.REPLACE_ONE);
  }

  @Override
  public boolean saveBatch(List<T> entityList) {
    if (CollectionUtils.isEmpty(entityList)) {
      return false;
    }
    List<List<T>> partition = Lists.partition(entityList, DEFAULT_BATCH_SIZE);
    for (List<T> list : partition) {
      getBaseMapper().insertBatchSomeColumn(list);
    }
    return true;
  }

  @Override
  public boolean updateBatch(List<T> entityList) {
    if (CollectionUtils.isEmpty(entityList)) {
      return false;
    }
    List<List<T>> partition = Lists.partition(entityList, DEFAULT_BATCH_SIZE);
    for (List<T> list : partition) {
      getBaseMapper().updateBatch(list);
    }
    return true;
  }

  private boolean saveBatch(Collection<T> entityList, int batchSize, CustomSqlMethod sqlMethod) {
    String sqlStatement = getSqlStatement(sqlMethod);
    executeBatch(entityList, batchSize, (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity));
    return true;
  }

  private String getSqlStatement(CustomSqlMethod sqlMethod) {
    return getMapperClass().getName() + StringPool.DOT + sqlMethod.getMethod();
  }
}
