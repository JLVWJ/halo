package com.lvwj.halo.common.utils;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.lvwj.halo.common.enums.BaseErrorEnum;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * DateTime 工具类
 */
public class DateTimeUtil extends LocalDateTimeUtil {

  public static final String PATTERN_DATETIME_S = "yyyy-MM-dd HH:mm:ss.SSS";
  public static final String PATTERN_DATETIME_M = "yyyy-MM-dd HH:mm";
  public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
  public static final String PATTERN_DATE = "yyyy-MM-dd";
  public static final String PATTERN_TIME = "HH:mm:ss";

  public static String PATTERN_Y_M_D_H_M_S_S = "yyyyMMddHHmmssSSS";
  public static String PATTERN_Y_M_D_H_M_S = "yyyyMMddHHmmss";
  public static String PATTERN_Y_M_D = "yyyyMMdd";
  public static String PATTERN_Y2_M_D = "yyMMdd";
  public static String PATTERN_Y_M = "yyyyMM";
  public static String PATTERN_M_D = "MMdd";
  public static String PATTERN_Y4 = "yyyy";
  public static String PATTERN_Y2 = "yy";
  public static String PATTERN_H_M_S = "HHmmss";
  public static String PATTERN_H_M = "HHmm";
  public static String PATTERN_M_S = "mmss";

  public static final DateTimeFormatter FORMAT_Y_M_D_H_M_S_S = DateTimeFormatter.ofPattern(PATTERN_Y_M_D_H_M_S_S);
  public static final DateTimeFormatter FORMAT_Y_M_D_H_M_S = DateTimeFormatter.ofPattern(PATTERN_Y_M_D_H_M_S);
  public static final DateTimeFormatter FORMAT_Y_M_D = DateTimeFormatter.ofPattern(PATTERN_Y_M_D);
  public static final DateTimeFormatter FORMAT_H_M_S = DateTimeFormatter.ofPattern(PATTERN_H_M_S);
  public static final DateTimeFormatter FORMAT_Y2_M_D = DateTimeFormatter.ofPattern(PATTERN_Y2_M_D);

  public static final DateTimeFormatter DATETIME_S_FORMAT = DateTimeFormatter.ofPattern(PATTERN_DATETIME_S);
  public static final DateTimeFormatter DATETIME_M_FORMAT = DateTimeFormatter.ofPattern(PATTERN_DATETIME_M);
  public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern(PATTERN_DATETIME);
  public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(PATTERN_DATE);
  public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern(PATTERN_TIME);

  /**
   * 格式：yyyy-MM-dd HH:mm:ss
   */
  public static String formatDateTime(TemporalAccessor temporal) {
    return DATETIME_FORMAT.format(temporal);
  }

  /**
   * 格式：yyyy-MM-dd HH:mm:ss.SSS
   */
  public static String formatDateTimeS(TemporalAccessor temporal) {
    return DATETIME_S_FORMAT.format(temporal);
  }

  /**
   * 格式：yyyy-MM-dd HH:mm
   */
  public static String formatDateTimeM(TemporalAccessor temporal) {
    return DATETIME_M_FORMAT.format(temporal);
  }

  /**
   * 格式：yyyyMMddHHmmssSSS
   */
  public static String formatYMDHMSS(TemporalAccessor temporal) {
    return FORMAT_Y_M_D_H_M_S_S.format(temporal);
  }
  /**
   * 格式：yyyyMMddHHmmss
   */
  public static String formatYMDHMS(TemporalAccessor temporal) {
    return FORMAT_Y_M_D_H_M_S.format(temporal);
  }
  /**
   * 格式：yyyyMMdd
   */
  public static String formatYMD(TemporalAccessor temporal) {
    return FORMAT_Y_M_D.format(temporal);
  }
  /**
   * 格式：HHmmss
   */
  public static String formatHMS(TemporalAccessor temporal) {
    return FORMAT_H_M_S.format(temporal);
  }
  /**
   * 格式：yyMMdd
   */
  public static String formatY2MD(TemporalAccessor temporal) {
    return FORMAT_Y2_M_D.format(temporal);
  }

  /**
   * 格式：yyyy-MM-dd
   */
  public static String formatDate(TemporalAccessor temporal) {
    return DATE_FORMAT.format(temporal);
  }

  /**
   * 格式：HH:mm:ss
   */
  public static String formatTime(TemporalAccessor temporal) {
    return TIME_FORMAT.format(temporal);
  }

  /**
   * 日期格式化自定义
   *
   * @param temporal 时间
   * @param pattern  表达式
   * @return 格式化后的时间
   */
  public static String format(TemporalAccessor temporal, String pattern) {
    return DateTimeFormatter.ofPattern(pattern).format(temporal);
  }

  /**
   * 将字符串转换为时间
   *
   * @param dateStr 时间字符串
   * @param pattern 表达式
   * @return 时间
   */
  public static LocalDateTime parseDateTime(String dateStr, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return parseDateTime(dateStr, formatter);
  }

  /**
   * 将字符串转换为时间
   *
   * @param dateStr   时间字符串
   * @param formatter DateTimeFormatter
   * @return 时间
   */
  public static LocalDateTime parseDateTime(String dateStr, DateTimeFormatter formatter) {
    return LocalDateTime.parse(dateStr, formatter);
  }

  /**
   * 将字符串转换为时间
   *
   * @param dateStr 时间字符串
   * @return 时间
   */
  public static LocalDateTime parseDateTime(String dateStr) {
    return DateTimeUtil.parseDateTime(dateStr, DateTimeUtil.DATETIME_FORMAT);
  }

  /**
   * 时间戳转成LocalDateTime(UTC)
   *
   * @author lvweijie
   * @date 2024/8/7 20:22
   * @param timestamp 时间戳: System.currentTimeMillis()
   * @return java.time.LocalDateTime
   */
  public static LocalDateTime parseDateTime(Long timestamp) {
    Assert.isTrue(null != timestamp && timestamp > 0, BaseErrorEnum.PARAM_VALID_ERROR);
    return LocalDateTime.ofEpochSecond(timestamp / 1000, 0, ZoneOffset.UTC);
  }

  /**
   * 时间戳转成LocalDateTime(带时区)
   *
   * @author lvweijie
   * @date 2024/8/7 20:22
   * @param timestamp 时间戳: System.currentTimeMillis()
   * @param zoneId 时区
   * @return java.time.LocalDateTime
   */
  public static LocalDateTime parseDateTime(Long timestamp, ZoneId zoneId) {
    LocalDateTime localDateTime = parseDateTime(timestamp);
    return toDateTime(localDateTime, zoneId);
  }

  /**
   * 将字符串转换为时间
   *
   * @param dateStr 时间字符串
   * @param pattern 表达式
   * @return 时间
   */
  public static LocalDate parseDate(String dateStr, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return DateTimeUtil.parseDate(dateStr, formatter);
  }

  /**
   * 将字符串转换为时间
   *
   * @param dateStr   时间字符串
   * @param formatter DateTimeFormatter
   * @return 时间
   */
  public static LocalDate parseDate(String dateStr, DateTimeFormatter formatter) {
    return LocalDate.parse(dateStr, formatter);
  }

  /**
   * 将字符串转换为日期
   *
   * @param dateStr 时间字符串
   * @return 时间
   */
  public static LocalDate parseDate(String dateStr) {
    return DateTimeUtil.parseDate(dateStr, DateTimeUtil.DATE_FORMAT);
  }

  public static LocalDateTime plusDays(LocalDateTime localDateTime, Integer plusDays) {
    return localDateTime.plusDays(plusDays);
  }

  public static LocalDate plusDays(LocalDate localDate, Integer plusDays) {
    return localDate.plusDays(plusDays);
  }

  /**
   * 将字符串转换为时间
   *
   * @param dateStr 时间字符串
   * @param pattern 时间正则
   * @return 时间
   */
  public static LocalTime parseTime(String dateStr, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return DateTimeUtil.parseTime(dateStr, formatter);
  }

  /**
   * 将字符串转换为时间
   *
   * @param dateStr   时间字符串
   * @param formatter DateTimeFormatter
   * @return 时间
   */
  public static LocalTime parseTime(String dateStr, DateTimeFormatter formatter) {
    return LocalTime.parse(dateStr, formatter);
  }

  /**
   * 将字符串转换为时间
   *
   * @param dateStr 时间字符串
   * @return 时间
   */
  public static LocalTime parseTime(String dateStr) {
    return DateTimeUtil.parseTime(dateStr, DateTimeUtil.TIME_FORMAT);
  }

  /**
   * 时间转 Instant
   *
   * @param dateTime 时间
   * @return Instant
   */
  public static Instant toInstant(LocalDateTime dateTime, ZoneId zoneId) {
    return dateTime.atZone(Optional.ofNullable(zoneId).orElse(ZoneId.systemDefault())).toInstant();
  }

  public static Instant toInstant(LocalDateTime dateTime) {
    return toInstant(dateTime, null);
  }

  /**
   * 时间转时区
   *
   * @author lvweijie
   * @date 2024/8/5 21:33
   * @param dateTime 时间
   * @param zoneId  时区
   * @return java.time.LocalDateTime
   */
  public static LocalDateTime toDateTime(LocalDateTime dateTime, ZoneId zoneId) {
    return dateTime.atZone(Optional.ofNullable(zoneId).orElse(ZoneId.systemDefault())).toLocalDateTime();
  }

  /**
   * Instant 转 LocalDateTime
   *
   * @param instant Instant
   * @return Instant
   */
  public static LocalDateTime toDateTime(Instant instant, ZoneId zoneId) {
    return LocalDateTime.ofInstant(instant, Optional.ofNullable(zoneId).orElse(ZoneId.systemDefault()));
  }

  public static LocalDateTime toDateTime(Instant instant) {
    return toDateTime(instant, null);
  }


  /**
   * Date 转 LocalDateTime
   */
  public static LocalDateTime toDateTime(Date date, ZoneId zoneId) {
    if (null == date) return null;
    Instant instant = date.toInstant();
    return instant.atZone(Optional.ofNullable(zoneId).orElse(ZoneId.systemDefault())).toLocalDateTime();
  }

  public static LocalDateTime toDateTime(Date date) {
    return toDateTime(date, null);
  }

  /**
   * 转换成 date
   *
   * @param dateTime LocalDateTime
   * @return Date
   */
  public static Date toDate(LocalDateTime dateTime) {
    return Date.from(toInstant(dateTime));
  }

  public static Date toDate(LocalDateTime dateTime, ZoneId zoneId) {
    return Date.from(toInstant(dateTime, zoneId));
  }

  /**
   * 比较2个时间差，跨度比较小
   *
   * @param startInclusive 开始时间
   * @param endExclusive   结束时间
   * @return 时间间隔
   */
  public static Duration between(Temporal startInclusive, Temporal endExclusive) {
    return Duration.between(startInclusive, endExclusive);
  }

  /**
   * 比较2个时间差，跨度比较大，年月日为单位
   *
   * @param startDate 开始时间
   * @param endDate   结束时间
   * @return 时间间隔
   */
  public static Period between(LocalDate startDate, LocalDate endDate) {
    return Period.between(startDate, endDate);
  }

  public static Boolean inRange(LocalTime localTime, String hHmmStartTime, String hHmmEndTime) {
    LocalTime localStartTime = LocalTime.parse(hHmmStartTime);
    LocalTime localEndTime = LocalTime.parse(hHmmEndTime);
    return localTime.isAfter(localStartTime) && localTime.isBefore(localEndTime);
  }

  public static Long diffSeconds(LocalDateTime startTime, LocalDateTime endTime) {
    long betweenMs = Duration.between(startTime, endTime).toMillis();
    return TimeUnit.MILLISECONDS.toSeconds(betweenMs);
  }

  public static Long diffMinutes(LocalDateTime startTime, LocalDateTime endTime) {
    long betweenMs = Duration.between(startTime, endTime).toMillis();
    return TimeUnit.MILLISECONDS.toMinutes(betweenMs);
  }
}
