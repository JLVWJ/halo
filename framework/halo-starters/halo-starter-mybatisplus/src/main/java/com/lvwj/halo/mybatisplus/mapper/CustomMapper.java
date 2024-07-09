package com.lvwj.halo.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.lvwj.halo.common.models.entity.IEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自定义扩展Mapper
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2022-12-07 18:05
 */
public interface CustomMapper<T extends IEntity<?>> extends BaseMapper<T> {

  /**
   * 插入（批量）例: insert into table(,) values(,),(,),(,),(,)
   *
   * @param entityList 数据实体对象集合
   * @return 成功行数
   */
  int insertBatchSomeColumn(List<T> entityList);

  /**
   * 更新（批量）
   *
   * @param entityList 数据实体对象集合
   * @return 成功行数
   */
  int updateBatch(@Param(Constants.LIST) List<T> entityList);

  /**
   * 插入 如果表中已存在相同的记录，则忽略当前新数据
   *
   * @param entity 实体对象
   * @return 更改的条数
   */
  int insertIgnore(T entity);

  /**
   * 插入替换数据，需求表中有PrimaryKey，或者unique索引，如果数据库已经存在数据，则用新数据替换，如果没有数据效果则和insert into一样；
   *
   * @param entity 实体对象
   * @return 更改的条数
   */
  int replace(T entity);
}
