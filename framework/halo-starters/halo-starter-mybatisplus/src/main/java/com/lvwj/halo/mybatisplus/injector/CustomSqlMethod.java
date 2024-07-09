package com.lvwj.halo.mybatisplus.injector;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 扩展的自定义方法
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2022-12-19 18:15
 */
@Getter
@AllArgsConstructor
public enum CustomSqlMethod {

  /**
   * 插入 如果表中已存在相同的记录，则忽略当前新数据
   */
  INSERT_IGNORE_ONE("insertIgnore", "插入一条数据（选择字段插入）", "<script>\nINSERT IGNORE INTO %s %s VALUES %s\n</script>"),

  /**
   * 插入替换数据，需求表中有PrimaryKey，或者unique索引，如果数据库已经存在数据，则用新数据替换，如果没有数据效果则和insert into一样；
   */
  REPLACE_ONE("replace", "插入一条数据（选择字段插入）", "<script>\nREPLACE INTO %s %s VALUES %s\n</script>"),

  /**
   * 合并update sql，批量更新(where条件是主键)
   */
  UPDATE_BATCH("updateBatch", "批量更新(where条件是主键)", "<script>\n<foreach collection=\"list\" item=\"item\" separator=\";\">\nupdate %s %s where %s=#{%s} %s\n</foreach>\n</script>");

  private final String method;
  private final String desc;
  private final String sql;
}
