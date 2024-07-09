package com.lvwj.halo.common.utils;

import com.lvwj.halo.common.enums.BaseErrorEnum;
import com.lvwj.halo.common.enums.IErrorEnum;
import com.lvwj.halo.common.exceptions.BusinessException;
import org.springframework.util.ObjectUtils;

import java.util.regex.Pattern;

/**
 * 异常断言类
 *
 * @author lvweijie
 * @date 2023/11/9 14:43
 */
public class Assert extends org.springframework.util.Assert {

  /**
   * 是否True
   *
   * @param expression 表达式
   * @param errorEnum  错误枚举
   * @author lvweijie
   * @date 2023/11/9 14:38
   */
  public static Boolean isTrue(boolean expression, IErrorEnum errorEnum) {
    if (!expression) {
      throw new BusinessException(errorEnum);
    }
    return true;
  }

  /**
   * 是否True
   *
   * @param expression 表达式
   * @param errorEnum  错误枚举
   * @param args       占位符参数
   * @author lvweijie
   * @date 2023/11/9 14:38
   */
  public static Boolean isTrue(boolean expression, IErrorEnum errorEnum, Object... args) {
    if (!expression) {
      throw new BusinessException(errorEnum, args);
    }
    return true;
  }

  /**
   * 是否False
   *
   * @param expression 表达式
   * @param errorEnum  错误枚举
   * @author lvweijie
   * @date 2023/11/9 14:38
   */
  public static Boolean isFalse(boolean expression, IErrorEnum errorEnum) {
    if (expression) {
      throw new BusinessException(errorEnum);
    }
    return true;
  }

  /**
   * 是否False
   *
   * @param expression 表达式
   * @param errorEnum  错误枚举
   * @param args       占位符参数
   * @author lvweijie
   * @date 2023/11/9 14:38
   */
  public static Boolean isFalse(boolean expression, IErrorEnum errorEnum, Object... args) {
    if (expression) {
      throw new BusinessException(errorEnum, args);
    }
    return true;
  }

  /**
   * 不能为null或空
   *
   * @param obj       可以是Optional、String、Map、Collection、Array等
   * @param errorEnum 错误枚举
   * @author lvweijie
   * @date 2023/11/9 14:38
   */
  public static Boolean notNullOrEmpty(Object obj, IErrorEnum errorEnum) {
    if (ObjectUtils.isEmpty(obj)) {
      throw new BusinessException(errorEnum);
    }
    return true;
  }

  /**
   * 不能为null或空
   *
   * @param obj       可以是Optional、String、Map、Collection、Array等
   * @param errorEnum 错误枚举
   * @param args      占位符参数
   * @author lvweijie
   * @date 2023/11/9 14:38
   */
  public static Boolean notNullOrEmpty(Object obj, IErrorEnum errorEnum, Object... args) {
    if (ObjectUtils.isEmpty(obj)) {
      throw new BusinessException(errorEnum, args);
    }
    return true;
  }

  /**
   * 判断是否为null或空
   *
   * @param obj       可以是Optional、String、Map、Collection、Array等
   * @param errorEnum 错误枚举
   * @author lvweijie
   * @date 2023/11/9 14:38
   */
  public static Boolean isNullOrEmpty(Object obj, IErrorEnum errorEnum) {
    if (!ObjectUtils.isEmpty(obj)) {
      throw new BusinessException(errorEnum);
    }
    return true;
  }

  /**
   * 判断是否为null或空
   *
   * @param obj       可以是Optional、String、Map、Collection、Array等
   * @param errorEnum 错误枚举
   * @param args      占位符参数
   * @author lvweijie
   * @date 2023/11/9 14:38
   */
  public static Boolean isNullOrEmpty(Object obj, IErrorEnum errorEnum, Object... args) {
    if (!ObjectUtils.isEmpty(obj)) {
      throw new BusinessException(errorEnum, args);
    }
    return true;
  }

  /**
   * obj是否是type的实例
   *
   * @param obj       对象
   * @param type      类型
   * @author lvweijie
   * @date 2023/11/9 14:41
   */
  public static <T> Boolean isInstance(Object obj, Class<T> type) {
    if (!type.isInstance(obj)) {
      throw new BusinessException(BaseErrorEnum.INSTANCE_TYPE_ERROR, obj.getClass().getName(), type.getName());
    }
    return true;
  }

  /**
   * 是否匹配正则
   *
   * @param input     输入符
   * @param pattern   正则表达式
   * @author lvweijie
   * @date 2023/11/9 14:42
   */
  public static Boolean matchPattern(CharSequence input, String pattern) {
    if (!Pattern.matches(pattern, input)) {
      throw new BusinessException(BaseErrorEnum.REG_EXP_ERROR, input, pattern);
    }
    return true;
  }
}
