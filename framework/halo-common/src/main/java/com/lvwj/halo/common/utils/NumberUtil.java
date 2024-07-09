package com.lvwj.halo.common.utils;


import org.springframework.lang.Nullable;
import org.springframework.util.NumberUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * 数字类型工具类
 */
public class NumberUtil extends NumberUtils {

  //-----------------------------------------------------------------------

  /**
   * <p>Convert a <code>String</code> to an <code>Integer</code>, returning
   * <code>null</code> if the conversion fails.</p>
   */
  public static Integer toInt(final String str) {
    return toInt(str, null);
  }

  /**
   * <p>Convert a <code>String</code> to an <code>Integer</code>, returning a
   * default value if the conversion fails.</p>
   *
   * <p>If the string is <code>null</code>, the default value is returned.</p>
   *
   * <pre>
   *   NumberUtil.toInt(null, 1) = 1
   *   NumberUtil.toInt("", 1)   = 1
   *   NumberUtil.toInt("1", 0)  = 1
   * </pre>
   *
   * @param str          the string to convert, may be null
   * @param defaultValue the default value
   * @return the int represented by the string, or the default if conversion fails
   */
  public static Integer toInt(@Nullable final String str, final Integer defaultValue) {
    if (str == null || str.isEmpty() || "null".equalsIgnoreCase(str)) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(str);
    } catch (final NumberFormatException nfe) {
      return defaultValue;
    }
  }

  /**
   * <p>Convert a <code>String</code> to a <code>Long</code>, returning
   * <code>null</code> if the conversion fails.</p>
   */
  public static Long toLong(final String str) {
    return toLong(str, null);
  }

  /**
   * <p>Convert a <code>String</code> to a <code>Long</code>, returning a
   * default value if the conversion fails.</p>
   *
   * <p>If the string is <code>null</code>, the default value is returned.</p>
   */
  public static Long toLong(@Nullable final String str, final Long defaultValue) {
    if (str == null || str.isEmpty() || "null".equalsIgnoreCase(str)) {
      return defaultValue;
    }
    try {
      return Long.parseLong(str);
    } catch (final NumberFormatException nfe) {
      return defaultValue;
    }
  }

  /**
   * <p>Convert a <code>String</code> to a <code>Double</code>
   *
   * @param value value
   * @return double value
   */
  public static Double toDouble(String value) {
    return toDouble(value, null);
  }

  /**
   * <p>Convert a <code>String</code> to a <code>Double</code>
   *
   * @param str          str
   * @param defaultValue 默认值
   * @return double value
   */
  public static Double toDouble(@Nullable String str, Double defaultValue) {
    if (str == null || "".equals(str) || "null".equalsIgnoreCase(str)) {
      return defaultValue;
    }
    try {
      return Double.valueOf(str.trim());
    } catch (final NumberFormatException nfe) {
      return defaultValue;
    }
  }

  /**
   * <p>Convert a <code>String</code> to a <code>Double</code>
   *
   * @param value value
   * @return double value
   */
  public static Float toFloat(String value) {
    return toFloat(value, null);
  }

  /**
   * <p>Convert a <code>String</code> to a <code>Double</code>
   *
   * @param str          str
   * @param defaultValue 默认值
   * @return double value
   */
  public static Float toFloat(@Nullable String str, Float defaultValue) {
    if (str == null || "".equals(str) || "null".equalsIgnoreCase(str)) {
      return defaultValue;
    }
    try {
      return Float.valueOf(str.trim());
    } catch (final NumberFormatException nfe) {
      return defaultValue;
    }
  }

  /**
   * All possible chars for representing a number as a String
   */
  private final static char[] DIGITS = {
      '0', '1', '2', '3', '4', '5',
      '6', '7', '8', '9', 'a', 'b',
      'c', 'd', 'e', 'f', 'g', 'h',
      'i', 'j', 'k', 'l', 'm', 'n',
      'o', 'p', 'q', 'r', 's', 't',
      'u', 'v', 'w', 'x', 'y', 'z',
      'A', 'B', 'C', 'D', 'E', 'F',
      'G', 'H', 'I', 'J', 'K', 'L',
      'M', 'N', 'O', 'P', 'Q', 'R',
      'S', 'T', 'U', 'V', 'W', 'X',
      'Y', 'Z'
  };

  /**
   * 将 long 转短字符串 为 62 进制
   *
   * @param i 数字
   * @return 短字符串
   */
  public static String to62String(long i) {
    int radix = DIGITS.length;
    char[] buf = new char[65];
    int charPos = 64;
    i = -i;
    while (i <= -radix) {
      buf[charPos--] = DIGITS[(int) (-(i % radix))];
      i = i / radix;
    }
    buf[charPos] = DIGITS[(int) (-i)];

    return new String(buf, charPos, (65 - charPos));
  }


  public static double mul(float v1, float v2) {
    return mul(Float.toString(v1), Float.toString(v2)).doubleValue();
  }

  public static double mul(double v1, float v2) {
    return mul(Double.toString(v1), Float.toString(v2)).doubleValue();
  }

  public static BigDecimal mul(Number v1, Number v2) {
    return mul(v1, v2);
  }

  public static BigDecimal mul(String v1, String v2) {
    return mul((Number) (new BigDecimal(v1)), (Number) (new BigDecimal(v2)));
  }

  public static double div(float v1, float v2) {
    return div(v1, v2, 10);
  }

  public static double div(float v1, float v2, int scale) {
    return div(v1, v2, scale, RoundingMode.HALF_UP);
  }

  public static double div(float v1, double v2, int scale) {
    return div(v1, v2, scale, RoundingMode.HALF_UP);
  }

  public static double div(double v1, float v2, int scale) {
    return div(v1, v2, scale, RoundingMode.HALF_UP);
  }

  public static double div(double v1, double v2, int scale) {
    return div(v1, v2, scale, RoundingMode.HALF_UP);
  }

  public static double div(Double v1, Double v2, int scale) {
    return div(v1, v2, scale, RoundingMode.HALF_UP);
  }

  public static BigDecimal div(Number v1, Number v2, int scale) {
    return div(v1, v2, scale, RoundingMode.HALF_UP);
  }

  public static BigDecimal div(String v1, String v2, int scale) {
    return div(v1, v2, scale, RoundingMode.HALF_UP);
  }

  public static double div(float v1, float v2, int scale, RoundingMode roundingMode) {
    return div(Float.toString(v1), Float.toString(v2), scale, roundingMode).doubleValue();
  }

  public static double div(float v1, double v2, int scale, RoundingMode roundingMode) {
    return div(Float.toString(v1), Double.toString(v2), scale, roundingMode).doubleValue();
  }

  public static double div(double v1, float v2, int scale, RoundingMode roundingMode) {
    return div(Double.toString(v1), Float.toString(v2), scale, roundingMode).doubleValue();
  }

  public static double div(double v1, double v2, int scale, RoundingMode roundingMode) {
    return div(Double.toString(v1), Double.toString(v2), scale, roundingMode).doubleValue();
  }

  public static double div(Double v1, Double v2, int scale, RoundingMode roundingMode) {
    return div((Number) v1, (Number) v2, scale, roundingMode).doubleValue();
  }

  public static BigDecimal div(Number v1, Number v2, int scale, RoundingMode roundingMode) {
    return v1 instanceof BigDecimal && v2 instanceof BigDecimal ? div((BigDecimal) v1,
        (BigDecimal) v2, scale, roundingMode)
        : div(v1.toString(), v2.toString(), scale, roundingMode);
  }

  public static BigDecimal div(String v1, String v2, int scale, RoundingMode roundingMode) {
    return div(toBigDecimal(v1), toBigDecimal(v2), scale, roundingMode);
  }

  public static BigDecimal div(BigDecimal v1, BigDecimal v2, int scale, RoundingMode roundingMode) {
    if (null == v1) {
      return BigDecimal.ZERO;
    } else {
      if (scale < 0) {
        scale = -scale;
      }

      return v1.divide(v2, scale, roundingMode);
    }
  }


  public static double toDouble(Number value) {
    return value instanceof Float ? Double.parseDouble(value.toString()) : value.doubleValue();
  }


  /**
   * 数字转{@link BigDecimal}<br> Float、Double等有精度问题，转换为字符串后再转换<br> null转换为0
   *
   * @param number 数字
   * @return {@link BigDecimal}
   * @since 4.0.9
   */
  public static BigDecimal toBigDecimal(Number number) {
    if (null == number) {
      return BigDecimal.ZERO;
    }

    if (number instanceof BigDecimal) {
      return (BigDecimal) number;
    } else if (number instanceof Long) {
      return new BigDecimal((Long) number);
    } else if (number instanceof Integer) {
      return new BigDecimal((Integer) number);
    } else if (number instanceof BigInteger) {
      return new BigDecimal((BigInteger) number);
    }

    // Float、Double等有精度问题，转换为字符串后再转换
    return toBigDecimal(number.toString());
  }

  /**
   * 数字转{@link BigDecimal}<br> null或""或空白符转换为0
   *
   * @param numberStr 数字字符串
   * @return {@link BigDecimal}
   * @since 4.0.9
   */
  public static BigDecimal toBigDecimal(String numberStr) {
    if (StringUtil.isBlank(numberStr)) {
      return BigDecimal.ZERO;
    }

    try {
      // 支持类似于 1,234.55 格式的数字
      final Number number = parseNumber(numberStr, Number.class);
      if (number instanceof BigDecimal) {
        return (BigDecimal) number;
      } else {
        return new BigDecimal(number.toString());
      }
    } catch (Exception ignore) {
      // 忽略解析错误
    }

    return new BigDecimal(numberStr);
  }

  /**
   * 把给定的总数平均分成N份，返回每份的个数<br> 当除以分数有余数时每份+1
   *
   * @param total     总数
   * @param partCount 份数
   * @return 每份的个数
   * @since 4.0.7
   */
  public static int partValue(int total, int partCount) {
    return partValue(total, partCount, true);
  }

  /**
   * 把给定的总数平均分成N份，返回每份的个数<br> 如果isPlusOneWhenHasRem为true，则当除以分数有余数时每份+1，否则丢弃余数部分
   *
   * @param total               总数
   * @param partCount           份数
   * @param isPlusOneWhenHasRem 在有余数时是否每份+1
   * @return 每份的个数
   * @since 4.0.7
   */
  public static int partValue(int total, int partCount, boolean isPlusOneWhenHasRem) {
    int partValue = total / partCount;
    if (isPlusOneWhenHasRem && total % partCount > 0) {
      partValue++;
    }
    return partValue;
  }

}
