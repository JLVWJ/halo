package com.lvwj.halo.number.model;

import com.lvwj.halo.number.constant.RuleTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 规则解析实体类
 *
 * @author lvwj
 * @date 2022-08-11 16:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleParseData {

  /**
   * 规则解析类型
   */
  private RuleTypeEnum type;
  /**
   * 键
   */
  private String key;
  /**
   * 值
   */
  private String value;
}
