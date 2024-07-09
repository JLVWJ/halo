package com.lvwj.halo.number.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 规则模式枚举
 *
 * @author lvwj
 * @date 2022-08-11 16:51
 */
@Getter
@AllArgsConstructor
@ToString
public enum RuleModeEnum {
  /**
   * 全局
   */
  G(10, "全局"),
  /**
   * 按天
   */
  D(20, "按天"),
  /**
   * 按月
   */
  M(30, "按月"),
  /**
   * 按年
   */
  Y(40, "按年"),
  ;

  /**
   * 编码
   */
  private final int code;

  /**
   * 名称
   */
  private final String name;

  public Boolean isDayMode() {
    return this.equals(RuleModeEnum.D);
  }

  public Boolean isMonthMode() {
    return this.equals(RuleModeEnum.M);
  }

  public Boolean isYearMode() {
    return this.equals(RuleModeEnum.Y);
  }
}
