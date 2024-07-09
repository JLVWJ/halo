package com.lvwj.halo.mybatisplus.injector.methods;


import com.lvwj.halo.mybatisplus.injector.CustomSqlMethod;

/**
 * 插入一条数据（选择字段插入）插入如果中已经存在相同的记录，则忽略当前新数据
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2022-12-19 18:15
 */
public class InsertIgnore extends AbstractInsertMethod {

  public InsertIgnore() {
    super(CustomSqlMethod.INSERT_IGNORE_ONE);
  }
}
