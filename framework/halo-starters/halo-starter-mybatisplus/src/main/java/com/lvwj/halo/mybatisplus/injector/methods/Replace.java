package com.lvwj.halo.mybatisplus.injector.methods;


import com.lvwj.halo.mybatisplus.injector.CustomSqlMethod;

/**
 * 插入一条数据（选择字段插入）
 * <p>
 * 表示插入替换数据，需求表中有PrimaryKey，或者unique索引，如果数据库已经存在数据，则用新数据替换，如果没有数据效果则和insert into一样；
 * </p>
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2022-12-19 18:16
 */
public class Replace extends AbstractInsertMethod {

  public Replace() {
    super(CustomSqlMethod.REPLACE_ONE);
  }
}

