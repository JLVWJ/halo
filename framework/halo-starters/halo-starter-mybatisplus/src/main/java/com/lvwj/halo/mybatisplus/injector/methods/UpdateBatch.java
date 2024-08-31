package com.lvwj.halo.mybatisplus.injector.methods;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.lvwj.halo.mybatisplus.injector.CustomSqlMethod;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 批量更新(优化版):一次数据库连接提交一批合并的update sql
 * 注意：mysql默认关闭foreach批处理，解决方法：mysql连接字符串加上"allowMultiQueries=true"
 * @author lvwj
 * @date 2023-01-08 23:22
 */
public class UpdateBatch extends AbstractMethod {

  public UpdateBatch() {
    super(CustomSqlMethod.UPDATE_BATCH.getMethod());
  }

  @Override
  public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
    String additional = (tableInfo.isWithVersion() ? "<if test=\"item != null and item['version'] != null\"> AND version=#{item.version}-1</if>"
            : "") + tableInfo.getLogicDeleteSql(true, true);
    String setSql = sqlSet(false, false, tableInfo, false, "item", "item.");
    String sqlResult = String.format(CustomSqlMethod.UPDATE_BATCH.getSql(), tableInfo.getTableName(), setSql,
            tableInfo.getKeyColumn(), "item." + tableInfo.getKeyProperty(), additional);
    SqlSource sqlSource = languageDriver.createSqlSource(configuration, sqlResult, modelClass);
    return this.addUpdateMappedStatement(mapperClass, modelClass, CustomSqlMethod.UPDATE_BATCH.getMethod(), sqlSource);
  }
}
