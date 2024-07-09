package com.lvwj.halo.common.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 生成的随机数类型
 */
@Getter
@RequiredArgsConstructor
public enum RandomType {

  INT(RandomType.INT_STR),
  LOWER(RandomType.LOWER_STR),
  UPPER(RandomType.UPPER_STR),
  LOWER_INT(RandomType.LOWER_INT_STR),
  UPPER_INT(RandomType.UPPER_INT_STR),
  ALL(RandomType.ALL_STR);

  private final String factor;

  private static final String INT_STR = "0123456789";
  private static final String LOWER_STR = "abcdefghijklmnopqrstuvwxyz";
  private static final String UPPER_STR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  private static final String LOWER_INT_STR = "abcdefghijklmnopqrstuvwxyz0123456789";
  private static final String UPPER_INT_STR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final String ALL_STR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
}
