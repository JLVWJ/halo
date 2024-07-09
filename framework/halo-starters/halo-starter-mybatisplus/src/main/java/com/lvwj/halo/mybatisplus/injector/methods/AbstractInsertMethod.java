package com.lvwj.halo.mybatisplus.injector.methods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import com.lvwj.halo.mybatisplus.injector.CustomSqlMethod;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 抽象的插入一条数据（选择字段插入）
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2022-12-21 10:55
 */
public abstract class AbstractInsertMethod extends AbstractMethod {

  private final CustomSqlMethod sqlMethod;

  public AbstractInsertMethod(CustomSqlMethod sqlMethod){
    super(sqlMethod.getMethod());
    this.sqlMethod = sqlMethod;
  }

  /**
   * 注入自定义 MappedStatement
   *
   * @author lvwj
   * @date 2022-12-21 10:47
   */
  @Override
  public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
    KeyGenerator keyGenerator = new NoKeyGenerator();
    String columnScript = SqlScriptUtils.convertTrim(tableInfo.getAllInsertSqlColumnMaybeIf(null),
        LEFT_BRACKET, RIGHT_BRACKET, null, COMMA);
    String valuesScript = SqlScriptUtils.convertTrim(tableInfo.getAllInsertSqlPropertyMaybeIf(null),
        LEFT_BRACKET, RIGHT_BRACKET, null, COMMA);
    String keyProperty = null;
    String keyColumn = null;
    // 表包含主键处理逻辑,如果不包含主键当普通字段处理
    if (tableInfo.havePK()) {
      if (tableInfo.getIdType() == IdType.AUTO) {
        // 自增主键
        keyGenerator = new Jdbc3KeyGenerator();
        keyProperty = tableInfo.getKeyProperty();
        keyColumn = tableInfo.getKeyColumn();
      } else {
        if (null != tableInfo.getKeySequence()) {
          keyGenerator = TableInfoHelper.genKeyGenerator(sqlMethod.getMethod(), tableInfo, builderAssistant);
          keyProperty = tableInfo.getKeyProperty();
          keyColumn = tableInfo.getKeyColumn();
        }
      }
    }
    String sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), columnScript, valuesScript);
    SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
    return this.addInsertMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource, keyGenerator,
        keyProperty, keyColumn);
  }
}
