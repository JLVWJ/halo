package com.lvwj.halo.mybatisplus.annotation;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * 注解：关联实体
 *
 * @author lvwj
 * @date 2022-12-08 18:25
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface JoinEntity {

  /**
   * 当前实体字段名的值是关联实体的主键id(例如：OrderItem上的ProductSnapshotId值(商品快照id)是关联实体ProductSnapshot上的主键id)
   * 当前实体和关联实体是一对一关系
   * 注意：是实体字段名，不是表字段名
   * @author lvwj
   * @date 2022-12-08 18:27
   */
  String primaryKey() default "";

  /**
   * 当前实体在关联实体上的外键实体字段名(例如:Order在OrderItem上是OrderId)
   * 当前实体和关联实体是一对一或一对多关系
   * 注意：是实体字段名，不是表字段名
   * @author lvwj
   * @date 2022-12-08 18:27
   */
  String foreignKey() default "";

  /**
   * 查询关联实体的额外条件
   * 例如: 主表关联查询明细表,额外条件是明细表的状态等于1且类型等于2，extraCondition="status=1 and type=2"
   * 例如: 主表关联查询明细表,额外条件是明细表的状态等于主表字段值，extraCondition="status=#orderStatus" (orderStatus是主表字段)
   * 例如: 主表关联查询明细表,额外条件是明细表的状态等于线程上下文的值，extraCondition="status=#status" (ThreadLocalUtil.get("#status"))
   */
  String extraCondition() default "";
}
