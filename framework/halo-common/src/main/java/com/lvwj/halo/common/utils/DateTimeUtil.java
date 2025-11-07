package com.lvwj.halo.common.utils;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.lvwj.halo.common.constants.DateTimeConstant;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * DateTime 工具类
 */
@Slf4j
public class DateTimeUtil extends LocalDateTimeUtil {

  /**
   * 私有构造，禁止实例化
   */
  private DateTimeUtil() {
    throw new AssertionError("工具类禁止实例化");
  }


  // ========================== 一、日期时间格式化 ==========================

  /**
   * 获取当前本地时间（无时区）
   *
   * @return 当前LocalDateTime
   * @example DateTimeUtil.now() → 2024-10-24T15:30:45
   */
  public static LocalDateTime now() {
    return LocalDateTime.now();
  }

  /**
   * 格式化：yyyy-MM-dd HH:mm:ss
   *
   * @param temporal 日期时间对象（支持LocalDateTime、LocalDate、ZonedDateTime等）
   * @return 格式化字符串
   * @example DateTimeUtil.formatDateTime(LocalDateTime.of ( 2024, 10, 24, 15, 30, 45)) → "2024-10-24 15:30:45"
   */
  public static String formatDateTime(TemporalAccessor temporal) {
    return format(temporal, DateTimeConstant.FORMAT_DATETIME);
  }

  public static String formatDateTime(TemporalAccessor temporal, ZoneId targetZone) {
    if (null == temporal)
      return null;
    if (temporal instanceof LocalDateTime) {
      temporal = toDateTime((LocalDateTime) temporal, targetZone);
    }
    return format(temporal, DateTimeConstant.FORMAT_DATETIME);
  }

  public static String formatDateTime(TemporalAccessor temporal, ZoneId sourceZone, ZoneId targetZone) {
    if (null == temporal)
      return null;
    if (temporal instanceof LocalDateTime) {
      temporal = toDateTime((LocalDateTime) temporal, sourceZone, targetZone);
    }
    return format(temporal, DateTimeConstant.FORMAT_DATETIME);
  }

  /**
   * 格式化：yyyy-MM-dd HH:mm:ss.SSS
   *
   * @param temporal 日期时间对象
   * @return 格式化字符串
   */
  public static String formatDateTimeS(TemporalAccessor temporal) {
    return format(temporal, DateTimeConstant.FORMAT_DATETIME_S);
  }

  /**
   * 格式化：yyyy-MM-dd HH:mm
   *
   * @param temporal 日期时间对象
   * @return 格式化字符串
   */
  public static String formatDateTimeM(TemporalAccessor temporal) {
    return format(temporal, DateTimeConstant.FORMAT_DATETIME_M);
  }

  /**
   * 格式化：yyyy-MM-dd
   *
   * @param temporal 日期对象（支持LocalDate、LocalDateTime等）
   * @return 格式化字符串
   */
  public static String formatDate(TemporalAccessor temporal) {
    return format(temporal, DateTimeConstant.FORMAT_DATE);
  }

  /**
   * 格式化：HH:mm:ss
   *
   * @param temporal 时间对象（支持LocalTime、LocalDateTime等）
   * @return 格式化字符串
   */
  public static String formatTime(TemporalAccessor temporal) {
    return format(temporal, DateTimeConstant.FORMAT_TIME);
  }

  /**
   * 格式化：yyyy-MM
   *
   * @param temporal 日期对象
   * @return 格式化字符串
   */
  public static String formatDateM(TemporalAccessor temporal) {
    return format(temporal, DateTimeConstant.FORMAT_DATE_M);
  }

  /**
   * 格式化：HH:mm
   *
   * @param temporal 时间对象
   * @return 格式化字符串
   */
  public static String formatTimeM(TemporalAccessor temporal) {
    return format(temporal, DateTimeConstant.FORMAT_TIME_M);
  }

  /**
   * 格式化：yyyyMMddHHmmssSSS（紧凑毫秒级）
   *
   * @param temporal 日期时间对象
   * @return 格式化字符串
   */
  public static String formatYMDHMSS(TemporalAccessor temporal) {
    return format(temporal, DateTimeConstant.FORMAT_Y_M_D_H_M_S_S);
  }

  /**
   * 格式化：yyyyMMddHHmmss（紧凑秒级）
   *
   * @param temporal 日期时间对象
   * @return 格式化字符串
   */
  public static String formatYMDHMS(TemporalAccessor temporal) {
    return format(temporal, DateTimeConstant.FORMAT_Y_M_D_H_M_S);
  }

  /**
   * 格式化：yyyyMMddHHmm（紧凑分钟级）
   *
   * @param temporal 日期时间对象
   * @return 格式化字符串
   */
  public static String formatYMDHM(TemporalAccessor temporal) {
    return format(temporal, DateTimeConstant.FORMAT_Y_M_D_H_M);
  }

  /**
   * 格式化：yyyyMMdd（紧凑日期）
   *
   * @param temporal 日期对象
   * @return 格式化字符串
   */
  public static String formatYMD(TemporalAccessor temporal) {
    return format(temporal, DateTimeConstant.FORMAT_Y_M_D);
  }

  /**
   * 格式化：yyyyMM（紧凑年月）
   *
   * @param temporal 日期对象
   * @return 格式化字符串
   */
  public static String formatYM(TemporalAccessor temporal) {
    return format(temporal, DateTimeConstant.FORMAT_Y_M);
  }

  /**
   * 格式化：HHmmss（紧凑时间）
   *
   * @param temporal 时间对象
   * @return 格式化字符串
   */
  public static String formatHMS(TemporalAccessor temporal) {
    return format(temporal, DateTimeConstant.FORMAT_H_M_S);
  }

  /**
   * 格式化：HHmm（紧凑小时分钟）
   *
   * @param temporal 时间对象
   * @return 格式化字符串
   */
  public static String formatHM(TemporalAccessor temporal) {
    return format(temporal, DateTimeConstant.FORMAT_H_M);
  }

  /**
   * 格式化：mmss（紧凑分钟秒）
   *
   * @param temporal 时间对象
   * @return 格式化字符串
   */
  public static String formatMS(TemporalAccessor temporal) {
    return format(temporal, DateTimeConstant.FORMAT_M_S);
  }

  /**
   * 格式化：yyMMdd（短年份日期）
   *
   * @param temporal 日期对象
   * @return 格式化字符串
   */
  public static String formatY2MD(TemporalAccessor temporal) {
    return format(temporal, DateTimeConstant.FORMAT_Y2_M_D);
  }

  /**
   * 格式化：时间戳转指定格式字符串
   *
   * @param timestamp 时间戳（毫秒级）
   * @param pattern   格式表达式
   * @return 格式化字符串
   * @example DateTimeUtil.formatTimestamp(1729759845000L, " yyyy - MM - dd HH : mm : ss ") → "2024-10-24 15:30:45"
   */
  public static String formatTimestamp(long timestamp, String pattern) {
    LocalDateTime dateTime = fromMilliseconds(timestamp);
    return format(dateTime, pattern);
  }

  /**
   * 自定义格式化
   *
   * @param temporal 日期时间对象
   * @param pattern  格式表达式（如"yyyy-MM-dd HH:mm:ss"）
   * @return 格式化字符串
   * @throws DateTimeParseException 格式表达式非法时抛出
   */
  public static String format(TemporalAccessor temporal, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return format(temporal, formatter);
  }

  /**
   * 自定义格式化（指定DateTimeFormatter）
   *
   * @param temporal  日期时间对象
   * @param formatter 格式化器
   * @return 格式化字符串
   */
  public static String format(TemporalAccessor temporal, DateTimeFormatter formatter) {
    try {
      if (null == temporal)
        return null;
      return formatter.format(temporal);
    } catch (DateTimeException e) {
      log.error("日期时间格式化失败，temporal: {}, formatter: {}", temporal, formatter, e);
      throw e;
    }
  }


  // ========================== 二、日期时间解析 ==========================

  /**
   * 解析：字符串转LocalDateTime（默认格式yyyy-MM-dd HH:mm:ss）
   *
   * @param dateStr 时间字符串
   * @return LocalDateTime
   * @throws DateTimeParseException 格式不匹配时抛出
   * @example DateTimeUtil.parseDateTime(" 2024 - 10 - 24 15 : 30 : 45 ") → 2024-10-24T15:30:45
   */
  public static LocalDateTime parseDateTime(String dateStr) {
    return parseDateTime(dateStr, DateTimeConstant.FORMAT_DATETIME);
  }

  /**
   * 解析：字符串转LocalDateTime（指定格式）
   *
   * @param dateStr 时间字符串
   * @param pattern 格式表达式
   * @return LocalDateTime
   */
  public static LocalDateTime parseDateTime(String dateStr, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return parseDateTime(dateStr, formatter);
  }

  /**
   * 解析：字符串转LocalDateTime（指定格式化器）
   *
   * @param dateStr   时间字符串
   * @param formatter 格式化器
   * @return LocalDateTime
   */
  public static LocalDateTime parseDateTime(String dateStr, DateTimeFormatter formatter) {
    try {
      return LocalDateTime.parse(dateStr, formatter);
    } catch (DateTimeParseException e) {
      log.error("解析LocalDateTime失败，dateStr: {}, formatter: {}", dateStr, formatter, e);
      throw e;
    }
  }

  /**
   * 解析：字符串转LocalDateTime（带时区转换，默认原始时区为系统时区）
   *
   * @param dateStr    时间字符串
   * @param targetZone 目标时区
   * @return 目标时区的LocalDateTime
   * @example 解析UTC时间字符串转东八区：
   * DateTimeUtil.parseDateTime("2024-10-24T07:30:45", DateTimeFormatter.ISO_LOCAL_DATE_TIME, ZoneId.of("UTC"), ZoneId.of("Asia/Shanghai")) → 2024-10-24T15:30:45
   */
  public static LocalDateTime parseDateTime(String dateStr, ZoneId targetZone) {
    return parseDateTime(dateStr, "", ZoneId.systemDefault(), targetZone);
  }

  /**
   * 解析：字符串转LocalDateTime（指定原始时区和目标时区）
   *
   * @param dateStr      时间字符串
   * @param originalZone 原始时区（字符串所属时区）
   * @param targetZone   目标时区
   * @return 目标时区的LocalDateTime
   */
  public static LocalDateTime parseDateTime(String dateStr, ZoneId originalZone, ZoneId targetZone) {
    return parseDateTime(dateStr, "", originalZone, targetZone);
  }

  /**
   * 解析：字符串转LocalDateTime（指定格式化器和目标时区）
   *
   * @param dateStr    时间字符串
   * @param formatter  格式化器
   * @param targetZone 目标时区
   * @return 目标时区的LocalDateTime
   */
  public static LocalDateTime parseDateTime(String dateStr, DateTimeFormatter formatter, ZoneId targetZone) {
    return parseDateTime(dateStr, formatter, ZoneId.systemDefault(), targetZone);
  }

  /**
   * 解析：字符串转LocalDateTime（指定格式化器、原始时区、目标时区）
   *
   * @param dateStr      时间字符串
   * @param formatter    格式化器（可为null，默认yyyy-MM-dd HH:mm:ss）
   * @param originalZone 原始时区（可为null，默认系统时区）
   * @param targetZone   目标时区（可为null，默认系统时区）
   * @return 目标时区的LocalDateTime
   */
  public static LocalDateTime parseDateTime(String dateStr, DateTimeFormatter formatter, ZoneId originalZone, ZoneId targetZone) {
    formatter = Optional.ofNullable(formatter).orElse(DateTimeConstant.FORMAT_DATETIME);
    LocalDateTime localDateTime = parseDateTime(dateStr, formatter);
    return toDateTime(localDateTime, originalZone, targetZone);
  }

  /**
   * 解析：字符串转LocalDateTime（指定格式、原始时区、目标时区）
   *
   * @param dateStr      时间字符串
   * @param pattern      格式表达式（可为null，默认yyyy-MM-dd HH:mm:ss）
   * @param originalZone 原始时区
   * @param targetZone   目标时区
   * @return 目标时区的LocalDateTime
   */
  public static LocalDateTime parseDateTime(String dateStr, String pattern, ZoneId originalZone, ZoneId targetZone) {
    DateTimeFormatter formatter = Func.isBlank(pattern) ? DateTimeConstant.FORMAT_DATETIME : DateTimeFormatter.ofPattern(pattern);
    return parseDateTime(dateStr, formatter, originalZone, targetZone);
  }

  /**
   * 解析：字符串转LocalDate（默认格式yyyy-MM-dd）
   *
   * @param dateStr 日期字符串
   * @return LocalDate
   * @example DateTimeUtil.parseDate(" 2024 - 10 - 24 ") → 2024-10-24
   */
  public static LocalDate parseDate(String dateStr) {
    return parseDate(dateStr, DateTimeConstant.FORMAT_DATE);
  }

  /**
   * 解析：字符串转LocalDate（指定格式）
   *
   * @param dateStr 日期字符串
   * @param pattern 格式表达式
   * @return LocalDate
   */
  public static LocalDate parseDate(String dateStr, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return parseDate(dateStr, formatter);
  }

  /**
   * 解析：字符串转LocalDate（指定格式化器）
   *
   * @param dateStr   日期字符串
   * @param formatter 格式化器
   * @return LocalDate
   */
  public static LocalDate parseDate(String dateStr, DateTimeFormatter formatter) {
    try {
      return LocalDate.parse(dateStr, formatter);
    } catch (DateTimeParseException e) {
      log.error("解析LocalDate失败，dateStr: {}, formatter: {}", dateStr, formatter, e);
      throw e;
    }
  }

  /**
   * 解析：字符串转LocalTime（默认格式HH:mm:ss）
   *
   * @param timeStr 时间字符串
   * @return LocalTime
   * @example DateTimeUtil.parseTime(" 15 : 30 : 45 ") → 15:30:45
   */
  public static LocalTime parseTime(String timeStr) {
    return parseTime(timeStr, DateTimeConstant.FORMAT_TIME);
  }

  /**
   * 解析：字符串转LocalTime（指定格式）
   *
   * @param timeStr 时间字符串
   * @param pattern 格式表达式
   * @return LocalTime
   */
  public static LocalTime parseTime(String timeStr, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return parseTime(timeStr, formatter);
  }

  /**
   * 解析：字符串转LocalTime（指定格式化器）
   *
   * @param timeStr   时间字符串
   * @param formatter 格式化器
   * @return LocalTime
   */
  public static LocalTime parseTime(String timeStr, DateTimeFormatter formatter) {
    try {
      return LocalTime.parse(timeStr, formatter);
    } catch (DateTimeParseException e) {
      log.error("解析LocalTime失败，timeStr: {}, formatter: {}", timeStr, formatter, e);
      throw e;
    }
  }

  /**
   * 解析：时间戳转LocalDateTime（默认系统时区）
   *
   * @param timestamp 时间戳（毫秒级，System.currentTimeMillis()）
   * @return LocalDateTime
   * @example DateTimeUtil.parseDateTime(1729759845000L) → 2024-10-24T15:30:45
   */
  public static LocalDateTime parseDateTime(long timestamp) {
    return fromMilliseconds(timestamp);
  }

  /**
   * 解析：时间戳转LocalDateTime（指定时区）
   *
   * @param timestamp 时间戳（毫秒级）
   * @param zoneId    目标时区
   * @return LocalDateTime
   */
  public static LocalDateTime parseDateTime(long timestamp, ZoneId zoneId) {
    return fromMilliseconds(timestamp, zoneId);
  }


  // ========================== 三、时间类型转换 ==========================

  /**
   * 转换：LocalDateTime → Instant（指定时区）
   *
   * @param dateTime 本地时间
   * @param zoneId   时区（可为null，默认系统时区）
   * @return Instant
   */
  public static Instant toInstant(LocalDateTime dateTime, ZoneId zoneId) {
    ZoneId targetZone = getDefaultZoneId(zoneId);
    return dateTime.atZone(targetZone).toInstant();
  }

  /**
   * 转换：LocalDateTime → Instant（系统默认时区）
   *
   * @param dateTime 本地时间
   * @return Instant
   */
  public static Instant toInstant(LocalDateTime dateTime) {
    return toInstant(dateTime, null);
  }

  /**
   * 转换：Instant → LocalDateTime（指定时区）
   *
   * @param instant 时间戳对象
   * @param zoneId  时区（可为null，默认系统时区）
   * @return LocalDateTime
   */
  public static LocalDateTime toDateTime(Instant instant, ZoneId zoneId) {
    ZoneId targetZone = getDefaultZoneId(zoneId);
    return instant.atZone(targetZone).toLocalDateTime();
  }

  /**
   * 转换：Instant → LocalDateTime（系统默认时区）
   *
   * @param instant 时间戳对象
   * @return LocalDateTime
   */
  public static LocalDateTime toDateTime(Instant instant) {
    return toDateTime(instant, null);
  }

  /**
   * 转换：Instant → LocalDateTime（指定原始时区和目标时区）
   *
   * @param instant      Instant对象
   * @param originalZone 原始时区（可为null，默认系统时区）
   * @param targetZone   目标时区（可为null，默认系统时区）
   * @return LocalDateTime
   */
  public static LocalDateTime toDateTime(Instant instant, ZoneId originalZone, ZoneId targetZone) {
    ZoneId origZone = getDefaultZoneId(originalZone);
    ZoneId destZone = getDefaultZoneId(targetZone);
    return instant.atZone(origZone).withZoneSameInstant(destZone).toLocalDateTime();
  }

  /**
   * 转换：Date → LocalDateTime（指定时区）
   *
   * @param date   旧日期对象（可为null，返回null）
   * @param zoneId 时区（可为null，默认系统时区）
   * @return LocalDateTime
   */
  public static LocalDateTime toDateTime(Date date, ZoneId zoneId) {
    if (date == null) {
      return null;
    }
    ZoneId targetZone = getDefaultZoneId(zoneId);
    return date.toInstant().atZone(targetZone).toLocalDateTime();
  }

  /**
   * 转换：Date → LocalDateTime（系统默认时区）
   *
   * @param date 旧日期对象（可为null，返回null）
   * @return LocalDateTime
   */
  public static LocalDateTime toDateTime(Date date) {
    return toDateTime(date, null);
  }

  /**
   * 转换：Date → LocalDateTime（指定原始时区和目标时区）
   *
   * @param date         旧日期对象（可为null，返回null）
   * @param originalZone 原始时区（可为null，默认系统时区）
   * @param targetZone   目标时区（可为null，默认系统时区）
   * @return LocalDateTime
   */
  public static LocalDateTime toDateTime(Date date, ZoneId originalZone, ZoneId targetZone) {
    if (date == null) {
      return null;
    }
    return toDateTime(date.toInstant(), originalZone, targetZone);
  }

  /**
   * 转换：LocalDateTime → Date（指定时区）
   *
   * @param dateTime 本地时间
   * @param zoneId   时区（可为null，默认系统时区）
   * @return Date
   */
  public static Date toDate(LocalDateTime dateTime, ZoneId zoneId) {
    Instant instant = toInstant(dateTime, zoneId);
    return Date.from(instant);
  }

  /**
   * 转换：LocalDateTime → Date（系统默认时区）
   *
   * @param dateTime 本地时间
   * @return Date
   */
  public static Date toDate(LocalDateTime dateTime) {
    return toDate(dateTime, null);
  }

  /**
   * 转换：LocalDate → Date（指定时区，默认当天00:00:00）
   *
   * @param localDate 本地日期
   * @param zoneId    时区（可为null，默认系统时区）
   * @return Date
   */
  public static Date toDate(LocalDate localDate, ZoneId zoneId) {
    if (localDate == null) {
      return null;
    }
    LocalDateTime dateTime = localDate.atStartOfDay();
    return toDate(dateTime, zoneId);
  }

  /**
   * 转换：LocalDate → Date（系统默认时区，当天00:00:00）
   *
   * @param localDate 本地日期
   * @return Date
   */
  public static Date toDate(LocalDate localDate) {
    return toDate(localDate, null);
  }

  /**
   * 转换：LocalDateTime → 毫秒时间戳（指定时区）
   *
   * @param dateTime 本地时间
   * @param zoneId   时区（可为null，默认系统时区）
   * @return 毫秒时间戳
   */
  public static long toMilliseconds(LocalDateTime dateTime, ZoneId zoneId) {
    Instant instant = toInstant(dateTime, zoneId);
    return instant.toEpochMilli();
  }

  /**
   * 转换：LocalDateTime → 毫秒时间戳（系统默认时区）
   *
   * @param dateTime 本地时间
   * @return 毫秒时间戳
   */
  public static long toMilliseconds(LocalDateTime dateTime) {
    return toMilliseconds(dateTime, null);
  }

  /**
   * 转换：LocalDate → 毫秒时间戳（指定时区，当天00:00:00）
   *
   * @param localDate 本地日期
   * @param zoneId    时区（可为null，默认系统时区）
   * @return 毫秒时间戳
   */
  public static long toMilliseconds(LocalDate localDate, ZoneId zoneId) {
    LocalDateTime dateTime = localDate.atStartOfDay();
    return toMilliseconds(dateTime, zoneId);
  }

  /**
   * 转换：LocalDate → 毫秒时间戳（系统默认时区，当天00:00:00）
   *
   * @param localDate 本地日期
   * @return 毫秒时间戳
   */
  public static long toMilliseconds(LocalDate localDate) {
    return toMilliseconds(localDate, null);
  }

  /**
   * 转换：毫秒时间戳 → LocalDateTime（指定时区）
   *
   * @param milliseconds 毫秒时间戳
   * @param zoneId       时区（可为null，默认系统时区）
   * @return LocalDateTime
   */
  public static LocalDateTime fromMilliseconds(long milliseconds, ZoneId zoneId) {
    ZoneId targetZone = getDefaultZoneId(zoneId);
    return Instant.ofEpochMilli(milliseconds).atZone(targetZone).toLocalDateTime();
  }

  /**
   * 转换：毫秒时间戳 → LocalDateTime（系统默认时区）
   *
   * @param milliseconds 毫秒时间戳
   * @return LocalDateTime
   */
  public static LocalDateTime fromMilliseconds(long milliseconds) {
    return fromMilliseconds(milliseconds, null);
  }


  // ========================== 四、时区转换 ==========================

  /**
   * 时区转换：LocalDateTime → 目标时区LocalDateTime（默认原始时区为系统时区）
   *
   * @param dateTime   原始本地时间
   * @param targetZone 目标时区（可为null，默认系统时区）
   * @return 目标时区的LocalDateTime
   * @example 东八区转UTC：
   * DateTimeUtil.toDateTime(LocalDateTime.of(2024,10,24,15,30,45), ZoneId.of("Asia/Shanghai"), ZoneId.of("UTC")) → 2024-10-24T07:30:45
   */
  public static LocalDateTime toDateTime(LocalDateTime dateTime, ZoneId targetZone) {
    return toDateTime(dateTime, ZoneId.systemDefault(), targetZone);
  }

  /**
   * 时区转换：LocalDateTime → 目标时区LocalDateTime（指定原始时区和目标时区）
   *
   * @param dateTime     原始本地时间（视为原始时区的时间）
   * @param originalZone 原始时区（可为null，默认系统时区）
   * @param targetZone   目标时区（可为null，默认系统时区）
   * @return 目标时区的LocalDateTime
   */
  public static LocalDateTime toDateTime(LocalDateTime dateTime, ZoneId originalZone, ZoneId targetZone) {
    if (dateTime == null)
      return null;
    ZoneId origZone = getDefaultZoneId(originalZone);
    ZoneId destZone = getDefaultZoneId(targetZone);
    // 原始时间关联原始时区 → 转换为目标时区同一时刻 → 提取LocalDateTime
    return dateTime.atZone(origZone).withZoneSameInstant(destZone).toLocalDateTime();
  }


  // ========================== 五、时间计算与比较 ==========================

  /**
   * 比较：两个时间的间隔（短跨度，支持纳秒到天，返回Duration）
   *
   * @param startInclusive 开始时间（如LocalDateTime、LocalTime）
   * @param endExclusive   结束时间
   * @return Duration 时间间隔
   * @example Duration.between(LocalTime.of ( 10, 0), LocalTime.of(12,30)) → PT2H30M（2小时30分钟）
   */
  public static Duration between(Temporal startInclusive, Temporal endExclusive) {
    return Duration.between(startInclusive, endExclusive);
  }

  /**
   * 比较：两个日期的间隔（长跨度，以年月日为单位，返回Period）
   *
   * @param startDate 开始日期
   * @param endDate   结束日期
   * @return Period 日期间隔
   * @example Period.between(LocalDate.of ( 2020, 1, 1), LocalDate.of(2024,10,24)) → P4Y9M23D（4年9个月23天）
   */
  public static Period between(LocalDate startDate, LocalDate endDate) {
    return Period.between(startDate, endDate);
  }

  /**
   * 比较：两个时间点的间隔年数（基于日历，忽略小于年的单位）
   * <p>适用于含年份信息的Temporal类型（如LocalDate、LocalDateTime、ZonedDateTime），计算完整的年份差</p>
   *
   * @param start 开始时间点（非空，需包含年份信息，如LocalDate）
   * @param end   结束时间点（非空，类型需与start一致，否则可能抛出DateTimeException）
   * @return 间隔年数（end - start的完整年份，可为负数）
   * @throws NullPointerException 若start或end为null
   * @throws DateTimeException    若类型不支持年份计算（如传入LocalTime）
   * @example ① LocalDate.of(2020, 1, 1) 与 LocalDate.of(2024, 10, 24) → 4年
   * @example ② LocalDate.of(2024, 3, 1) 与 LocalDate.of(2024, 6, 1) → 0年（不足1年）
   */
  public static long betweenYears(Temporal start, Temporal end) {
    Objects.requireNonNull(start, "开始时间点[start]不能为null");
    Objects.requireNonNull(end, "结束时间点[end]不能为null");
    return ChronoUnit.YEARS.between(start, end);
  }

  /**
   * 比较：两个时间点的间隔月数（基于日历，忽略小于月的单位）
   * <p>适用于含年月信息的Temporal类型（如LocalDate、LocalDateTime），计算完整的月份差</p>
   *
   * @param start 开始时间点（非空，需包含年月信息）
   * @param end   结束时间点（非空，类型需与start一致）
   * @return 间隔月数（end - start的完整月份，可为负数）
   * @throws NullPointerException 若start或end为null
   * @throws DateTimeException    若类型不支持月份计算（如传入LocalTime）
   * @example ① LocalDate.of(2024, 1, 15) 与 LocalDate.of(2024, 4, 20) → 3个月（1月到4月）
   * @example ② LocalDate.of(2024, 4, 20) 与 LocalDate.of(2024, 4, 25) → 0个月（不足1个月）
   */
  public static long betweenMonths(Temporal start, Temporal end) {
    Objects.requireNonNull(start, "开始时间点[start]不能为null");
    Objects.requireNonNull(end, "结束时间点[end]不能为null");
    return ChronoUnit.MONTHS.between(start, end);
  }

  /**
   * 比较：两个时间点的间隔天数（基于日历，忽略小于天的单位，仅算日期差）
   * <p>适用于含日期信息的Temporal类型（如LocalDate、LocalDateTime），等价于“end的日期 - start的日期”</p>
   *
   * @param start 开始时间点（非空，需包含日期信息）
   * @param end   结束时间点（非空，类型需与start一致）
   * @return 间隔天数（end - start的完整天数，可为负数）
   * @throws NullPointerException 若start或end为null
   * @throws DateTimeException    若类型不支持日期计算（如传入LocalTime）
   * @example ① LocalDate.of(2024, 10, 24) 与 LocalDate.of(2024, 10, 26) → 2天
   * @example ② LocalDateTime.of(2024, 10, 24, 23, 59) 与 LocalDateTime.of(2024, 10, 25, 0, 1) → 1天（忽略时间）
   */
  public static long betweenDays(Temporal start, Temporal end) {
    Objects.requireNonNull(start, "开始时间点[start]不能为null");
    Objects.requireNonNull(end, "结束时间点[end]不能为null");
    return ChronoUnit.DAYS.between(start, end);
  }

  /**
   * 比较：两个时间点的间隔小时数（基于24小时制，忽略小于小时的单位）
   * <p>适用于含小时信息的Temporal类型（如LocalDateTime、LocalTime、ZonedDateTime），计算完整的小时差</p>
   *
   * @param start 开始时间点（非空，需包含小时信息）
   * @param end   结束时间点（非空，类型需与start一致）
   * @return 间隔小时数（end - start的完整小时，可为负数）
   * @throws NullPointerException 若start或end为null
   * @throws DateTimeException    若类型不支持小时计算（如传入LocalDate）
   * @example ① LocalTime.of(10, 30) 与 LocalTime.of(13, 15) → 2小时（10:30到13:15共2.75小时，取整为2）
   * @example ② LocalDateTime.of(2024, 10, 24, 23, 0) 与 LocalDateTime.of(2024, 10, 25, 1, 0) → 2小时
   */
  public static long betweenHours(Temporal start, Temporal end) {
    Objects.requireNonNull(start, "开始时间点[start]不能为null");
    Objects.requireNonNull(end, "结束时间点[end]不能为null");
    return ChronoUnit.HOURS.between(start, end);
  }

  /**
   * 比较：两个时间点的间隔分钟数（基于60分钟制，忽略小于分钟的单位）
   * <p>适用于含分钟信息的Temporal类型（如LocalDateTime、LocalTime），计算完整的分钟差</p>
   *
   * @param start 开始时间点（非空，需包含分钟信息）
   * @param end   结束时间点（非空，类型需与start一致）
   * @return 间隔分钟数（end - start的完整分钟，可为负数）
   * @throws NullPointerException 若start或end为null
   * @throws DateTimeException    若类型不支持分钟计算（如传入LocalDate）
   * @example ① LocalTime.of(10, 15) 与 LocalTime.of(10, 40) → 25分钟
   * @example ② LocalTime.of(10, 40, 59) 与 LocalTime.of(10, 41, 1) → 0分钟（不足1分钟）
   */
  public static long betweenMinutes(Temporal start, Temporal end) {
    Objects.requireNonNull(start, "开始时间点[start]不能为null");
    Objects.requireNonNull(end, "结束时间点[end]不能为null");
    return ChronoUnit.MINUTES.between(start, end);
  }

  /**
   * 比较：两个时间点的间隔秒数（基于60秒制，忽略纳秒单位）
   * <p>适用于含秒信息的Temporal类型（如LocalDateTime、LocalTime），计算完整的秒差</p>
   *
   * @param start 开始时间点（非空，需包含秒信息）
   * @param end   结束时间点（非空，类型需与start一致）
   * @return 间隔秒数（end - start的完整秒，可为负数）
   * @throws NullPointerException 若start或end为null
   * @throws DateTimeException    若类型不支持秒计算（如传入LocalDate）
   * @example ① LocalTime.of(10, 0, 10) 与 LocalTime.of(10, 0, 30) → 20秒
   * @example ② LocalTime.of(10, 0, 30, 500_000_000) 与 LocalTime.of(10, 0, 31, 100_000_000) → 0秒（不足1秒）
   */
  public static long betweenSeconds(Temporal start, Temporal end) {
    Objects.requireNonNull(start, "开始时间点[start]不能为null");
    Objects.requireNonNull(end, "结束时间点[end]不能为null");
    return ChronoUnit.SECONDS.between(start, end);
  }

  public static long betweenMillis(Temporal start, Temporal end) {
    Objects.requireNonNull(start, "开始时间点[start]不能为null");
    Objects.requireNonNull(end, "结束时间点[end]不能为null");
    return ChronoUnit.MILLIS.between(start, end);
  }

  /**
   * 判断：时间是否在指定时间段内（HHmm格式，左开右开）
   *
   * @param localTime     待判断时间
   * @param hHmmStartTime 开始时间（HHmm格式，如"0900"）
   * @param hHmmEndTime   结束时间（HHmm格式，如"1800"）
   * @return true：在时间段内；false：不在
   * @fix 原代码bug：补充HHmm格式解析器
   */
  public static Boolean inRange(LocalTime localTime, String hHmmStartTime, String hHmmEndTime) {
    LocalTime start = parseTime(hHmmStartTime, DateTimeConstant.PATTERN_H_M);
    LocalTime end = parseTime(hHmmEndTime, DateTimeConstant.PATTERN_H_M);
    // 左开右开：排除开始和结束时刻
    return localTime.isAfter(start) && localTime.isBefore(end);
  }

  /**
   * 判断：时间是否在指定时间段内（包含边界）
   *
   * @param localTime 待判断时间
   * @param startTime 开始时间
   * @param endTime   结束时间
   * @return true：在时间段内；false：不在
   */
  public static Boolean inRange(LocalTime localTime, LocalTime startTime, LocalTime endTime) {
    return !localTime.isBefore(startTime) && !localTime.isAfter(endTime);
  }

  /**
   * 判断：日期是否在指定日期段内（包含边界）
   *
   * @param localDate 待判断日期
   * @param startDate 开始日期
   * @param endDate   结束日期
   * @return true：在日期段内；false：不在
   */
  public static Boolean inRange(LocalDate localDate, LocalDate startDate, LocalDate endDate) {
    return !localDate.isBefore(startDate) && !localDate.isAfter(endDate);
  }

  /**
   * 判断：日期时间是否在指定时间段内（包含边界）
   *
   * @param dateTime  待判断日期时间
   * @param startTime 开始日期时间
   * @param endTime   结束日期时间
   * @return true：在时间段内；false：不在
   */
  public static Boolean inRange(LocalDateTime dateTime, LocalDateTime startTime, LocalDateTime endTime) {
    return !dateTime.isBefore(startTime) && !dateTime.isAfter(endTime);
  }

  /**
   * 判断：是否为今天
   *
   * @param dateTime 待判断日期时间
   * @return true：今天；false：不是
   */
  public static Boolean isToday(LocalDateTime dateTime) {
    LocalDate today = LocalDate.now();
    return dateTime.toLocalDate().equals(today);
  }

  /**
   * 判断：是否为昨天
   *
   * @param dateTime 待判断日期时间
   * @return true：昨天；false：不是
   */
  public static Boolean isYesterday(LocalDateTime dateTime) {
    LocalDate yesterday = LocalDate.now().minusDays(1);
    return dateTime.toLocalDate().equals(yesterday);
  }

  /**
   * 判断：是否为明天
   *
   * @param dateTime 待判断日期时间
   * @return true：明天；false：不是
   */
  public static Boolean isTomorrow(LocalDateTime dateTime) {
    LocalDate tomorrow = LocalDate.now().plusDays(1);
    return dateTime.toLocalDate().equals(tomorrow);
  }

  /**
   * 判断：是否为闰年
   *
   * @param year 年份
   * @return true：闰年；false：平年
   */
  public static Boolean isLeapYear(int year) {
    return Year.isLeap(year);
  }

  /**
   * 判断：指定日期所在年份是否为闰年
   *
   * @param localDate 日期
   * @return true：闰年；false：平年
   */
  public static Boolean isLeapYear(LocalDate localDate) {
    return localDate.isLeapYear();
  }

  /**
   * 时间加减：LocalDateTime 加周
   *
   * @param date       目标时间
   * @param weeksToAdd 增加的周数（负数表示减少）
   * @return 操作后的 LocalDateTime
   */
  public static LocalDateTime plusWeeks(LocalDateTime date, long weeksToAdd) {
    return plusDays(date, weeksToAdd * 7L);
  }

  /**
   * 时间加减：LocalDateTime 加天数
   *
   * @param localDateTime 原始时间
   * @param plusDays      增加的天数（可为负数，即减天数）
   * @return 计算后的LocalDateTime
   */
  public static LocalDateTime plusDays(LocalDateTime localDateTime, long plusDays) {
    return localDateTime.plusDays(plusDays);
  }

  /**
   * 时间加减：LocalDate 加天数
   *
   * @param localDate 原始日期
   * @param plusDays  增加的天数（可为负数，即减天数）
   * @return 计算后的LocalDate
   */
  public static LocalDate plusDays(LocalDate localDate, long plusDays) {
    return localDate.plusDays(plusDays);
  }

  /**
   * 时间加减：LocalDateTime 加小时
   *
   * @param localDateTime 原始时间
   * @param plusHours     增加的小时数（可为负数）
   * @return 计算后的LocalDateTime
   */
  public static LocalDateTime plusHours(LocalDateTime localDateTime, long plusHours) {
    return localDateTime.plusHours(plusHours);
  }

  /**
   * 时间加减：LocalDateTime 加分钟
   *
   * @param localDateTime 原始时间
   * @param plusMinutes   增加的分钟数（可为负数）
   * @return 计算后的LocalDateTime
   */
  public static LocalDateTime plusMinutes(LocalDateTime localDateTime, long plusMinutes) {
    return localDateTime.plusMinutes(plusMinutes);
  }

  /**
   * 时间加减：LocalDateTime 加秒
   *
   * @param localDateTime 原始时间
   * @param secondsToAdd  增加的秒数（可为负数）
   * @return 计算后的LocalDateTime
   */
  public static LocalDateTime plusSeconds(LocalDateTime localDateTime, long secondsToAdd) {
    return localDateTime.plusSeconds(secondsToAdd);
  }

  /**
   * 时间加减：LocalDateTime 加毫秒
   *
   * @param localDateTime 原始时间
   * @param millisToAdd   增加的毫秒数（负数表示减少）
   * @return 计算后的LocalDateTime
   */
  public static LocalDateTime plusMillis(LocalDateTime localDateTime, long millisToAdd) {
    return localDateTime.plusNanos(millisToAdd * 1_000_000);
  }

  /**
   * 时间加减：LocalDateTime 加纳秒
   *
   * @param localDateTime 原始时间
   * @param nanosToAdd    增加的纳秒数（负数表示减少）
   * @return 计算后的LocalDateTime
   */
  public static LocalDateTime plusNanos(LocalDateTime localDateTime, long nanosToAdd) {
    return localDateTime.plusNanos(nanosToAdd);
  }

  /**
   * 时间加减：LocalDateTime 加月份
   *
   * @param localDateTime 原始时间
   * @param plusMonths    增加的月份数（可为负数）
   * @return 计算后的LocalDateTime
   */
  public static LocalDateTime plusMonths(LocalDateTime localDateTime, long plusMonths) {
    return localDateTime.plusMonths(plusMonths);
  }

  /**
   * 时间加减：LocalDateTime 加年份
   *
   * @param localDateTime 原始时间
   * @param plusYears     增加的年份数（可为负数）
   * @return 计算后的LocalDateTime
   */
  public static LocalDateTime plusYears(LocalDateTime localDateTime, long plusYears) {
    return localDateTime.plusYears(plusYears);
  }

  /**
   * LocalDateTime 减少年
   *
   * @param date  目标时间
   * @param years 减少的年数
   * @return 操作后的 Date
   */
  public static LocalDateTime minusYears(LocalDateTime date, long years) {
    return plusYears(date, -years);
  }

  /**
   * LocalDateTime 减少月
   *
   * @param date   目标时间
   * @param months 减少的月数
   * @return 操作后的 Date
   */
  public static LocalDateTime minusMonths(LocalDateTime date, long months) {
    return plusMonths(date, -months);
  }

  /**
   * LocalDateTime 减少周
   *
   * @param date  目标时间
   * @param weeks 减少的周数
   * @return 操作后的 Date
   */
  public static LocalDateTime minusWeeks(LocalDateTime date, long weeks) {
    return plusWeeks(date, -weeks);
  }

  /**
   * LocalDateTime 减少天
   *
   * @param date 目标时间
   * @param days 减少的天数
   * @return 操作后的 Date
   */
  public static LocalDateTime minusDays(LocalDateTime date, long days) {
    return plusDays(date, -days);
  }

  /**
   * LocalDateTime 减少小时
   *
   * @param date  目标时间
   * @param hours 减少的小时数
   * @return 操作后的 Date
   */
  public static LocalDateTime minusHours(LocalDateTime date, long hours) {
    return plusHours(date, -hours);
  }

  /**
   * LocalDateTime 减少分钟
   *
   * @param date    目标时间
   * @param minutes 减少的分钟数
   * @return 操作后的 Date
   */
  public static LocalDateTime minusMinutes(LocalDateTime date, long minutes) {
    return plusMinutes(date, -minutes);
  }

  /**
   * LocalDateTime 减少秒
   *
   * @param date    目标时间
   * @param seconds 减少的秒数
   * @return 操作后的 Date
   */
  public static LocalDateTime minusSeconds(LocalDateTime date, long seconds) {
    return plusSeconds(date, -seconds);
  }

  /**
   * LocalDateTime 减少毫秒
   *
   * @param date   目标时间
   * @param millis 减少的毫秒数
   * @return 操作后的 Date
   */
  public static LocalDateTime minusMillis(LocalDateTime date, long millis) {
    return plusMillis(date, -millis);
  }

  /**
   * LocalDateTime 减少纳秒
   *
   * @param date  目标时间
   * @param nanos 减少的纳秒数
   * @return 操作后的 Date
   */
  public static LocalDateTime minusNanos(LocalDateTime date, long nanos) {
    return plusNanos(date, -nanos);
  }

  /**
   * 时间截断：截断到指定单位（如截断到小时，分钟秒置为00:00）
   *
   * @param dateTime 原始时间
   * @param unit     截断单位（如ChronoUnit.HOURS、ChronoUnit.MINUTES）
   * @return 截断后的LocalDateTime
   * @example DateTimeUtil.truncate(LocalDateTime.of ( 2024, 10, 24, 15, 30, 45), ChronoUnit.HOURS) → 2024-10-24T15:00:00
   */
  public static LocalDateTime truncate(LocalDateTime dateTime, ChronoUnit unit) {
    return dateTime.truncatedTo(unit);
  }

  /**
   * 获取：指定年月的天数
   *
   * @param year  年份
   * @param month 月份（1-12）
   * @return 该月天数
   */
  public static int getDaysInMonth(int year, int month) {
    return YearMonth.of(year, month).lengthOfMonth();
  }

  /**
   * 获取：指定日期所在月的天数
   *
   * @param localDate 日期
   * @return 该月天数
   */
  public static int getDaysInMonth(LocalDate localDate) {
    return localDate.lengthOfMonth();
  }


  // ========================== 六、常用时间点获取 ==========================

  /**
   * 获取：今年起始时间（1月1日 00:00:00.000）
   *
   * @return 今年起始LocalDateTime
   */
  public static LocalDateTime getYearStart() {
    return LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()), LocalTime.MIN);
  }

  /**
   * 获取：今年结束时间（12月31日 23:59:59.999）
   *
   * @return 今年结束LocalDateTime
   */
  public static LocalDateTime getYearEnd() {
    return LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.lastDayOfYear()), LocalTime.MAX);
  }

  /**
   * 获取：当前季度起始时间（季度第一天 00:00:00.000）
   *
   * @return 本季度起始LocalDateTime
   */
  public static LocalDateTime getQuarterStart() {
    LocalDate now = LocalDate.now();
    Month firstMonthOfQuarter = now.getMonth().firstMonthOfQuarter();
    LocalDate quarterStartDate = LocalDate.of(now.getYear(), firstMonthOfQuarter, 1);
    return LocalDateTime.of(quarterStartDate, LocalTime.MIN);
  }

  /**
   * 获取：当前季度结束时间（季度最后一天 23:59:59.999）
   *
   * @return 本季度结束LocalDateTime
   */
  public static LocalDateTime getQuarterEnd() {
    LocalDate now = LocalDate.now();
    Month lastMonthOfQuarter = now.getMonth().firstMonthOfQuarter().plus(2);
    LocalDate quarterEndDate = LocalDate.of(now.getYear(), lastMonthOfQuarter, lastMonthOfQuarter.maxLength());
    return LocalDateTime.of(quarterEndDate, LocalTime.MAX);
  }

  /**
   * 获取：当月起始时间（当月1日 00:00:00.000）
   *
   * @return 本月起始LocalDateTime
   */
  public static LocalDateTime getMonthStart() {
    LocalDate monthStartDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    return LocalDateTime.of(monthStartDate, LocalTime.MIN);
  }

  /**
   * 获取：当月起始时间（Date类型，兼容旧代码）
   *
   * @return 本月起始Date
   */
  public static Date getMonthStartAsDate() {
    return toDate(getMonthStart());
  }

  /**
   * 获取：当月结束时间（当月最后一天 23:59:59.999）
   *
   * @return 本月结束LocalDateTime
   */
  public static LocalDateTime getMonthEnd() {
    LocalDate monthEndDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    return LocalDateTime.of(monthEndDate, LocalTime.MAX);
  }

  /**
   * 获取：当周起始时间（周一 00:00:00.000，默认周一为一周第一天）
   *
   * @return 本周起始LocalDateTime
   */
  public static LocalDateTime getWeekStart() {
    LocalDate weekStartDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    return LocalDateTime.of(weekStartDate, LocalTime.MIN);
  }

  /**
   * 获取：当周结束时间（周日 23:59:59.999，默认周日为一周最后一天）
   *
   * @return 本周结束LocalDateTime
   */
  public static LocalDateTime getWeekEnd() {
    LocalDate weekEndDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    return LocalDateTime.of(weekEndDate, LocalTime.MAX);
  }

  /**
   * 获取：当天起始时间（00:00:00.000）
   *
   * @return 今日起始LocalDateTime
   */
  public static LocalDateTime getDayStart() {
    return getDayStart(LocalDate.now());
  }

  public static LocalDateTime getDayStart(LocalDate date) {
    Assert.notNull(date, "The date must not be null");
    return LocalDateTime.of(date, LocalTime.MIN);
  }

  /**
   * 获取：当天结束时间（23:59:59.999）
   *
   * @return 今日结束LocalDateTime
   */
  public static LocalDateTime getDayEnd() {
    return getDayEnd(LocalDate.now());
  }

  public static LocalDateTime getDayEnd(LocalDate date) {
    Assert.notNull(date, "The date must not be null");
    return LocalDateTime.of(date, LocalTime.MAX);
  }

  /**
   * 获取：当天中午时间（12:00:00）
   *
   * @return 今日中午LocalDateTime
   */
  public static LocalDateTime getDayNoon() {
    return LocalDateTime.of(LocalDate.now(), LocalTime.NOON);
  }

  /**
   * 获取：当天凌晨时间（03:00:00，可自定义小时）
   *
   * @param hour 凌晨小时（0-6）
   * @return 今日凌晨LocalDateTime
   */
  public static LocalDateTime getDayDawn(int hour) {
    if (hour < 0 || hour > 6) {
      throw new IllegalArgumentException("凌晨小时范围应为0-6");
    }
    return LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, 0));
  }


  // ========================== 私有工具方法 ==========================

  /**
   * 获取默认时区（为空时返回系统时区）
   *
   * @param zoneId 输入时区
   * @return 非空时区
   */
  private static ZoneId getDefaultZoneId(ZoneId zoneId) {
    return Optional.ofNullable(zoneId).orElse(ZoneId.systemDefault());
  }
}
