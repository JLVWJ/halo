package com.lvwj.halo.number.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 规则类型枚举
 *
 * @author lvwj
 * @date 2022-08-11 16:51
 */
@Getter
@AllArgsConstructor
@ToString
public enum RuleTypeEnum {
  /**
   * TEXT-10-普通文本
   */
  TEXT(10, "普通文本"),
  /**
   * DATE-20-日期时间
   */
  DATE(20, "日期时间"),
  /**
   * PARAMS-30-参数注入
   */
  PARAMS(30, "参数注入"),
  /**
   * FUNC-40-函数解析
   */
  FUNC(40, "函数解析"),
  ;

  /**
   * 编码
   */
  private final int code;

  /**
   * 名称
   */
  private final String name;

}
