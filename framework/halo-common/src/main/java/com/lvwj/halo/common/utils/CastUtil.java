package com.lvwj.halo.common.utils;

/**
 *
 * @author lvwj
 * @date 2022-08-15 16:02
 */
public class CastUtil extends cn.hutool.core.convert.CastUtil{

  public static <T> T cast(Object obj) {
    return (T) obj;
  }
}
