package com.lvwj.halo.common.utils;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.lvwj.halo.common.constants.DateTimeConstant;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * DateTime 工具类
 */
public class DateTimeUtil extends LocalDateTimeUtil {

  /**
   * 当前时间
   */
  public static LocalDateTime now() {
    return LocalDateTime.now();
  }

  /**
   * 格式：yyyy-MM-dd HH:mm:ss
   */
  public static String formatDateTime(TemporalAccessor temporal) {
    return DateTimeConstant.FORMAT_DATETIME.format(temporal);
  }

  /**
   * 格式：yyyy-MM-dd HH:mm:ss.SSS
   */
  public static String formatDateTimeS(TemporalAccessor temporal) {
    return DateTimeConstant.FORMAT_DATETIME_S.format(temporal);
  }

  /**
   * 格式：yyyy-MM-dd HH:mm
   */
  public static String formatDateTimeM(TemporalAccessor temporal) {
    return DateTimeConstant.FORMAT_DATETIME_M.format(temporal);
  }

  /**
   * 格式：yyyy-MM-dd
   */
  public static String formatDate(TemporalAccessor temporal) {
    return DateTimeConstant.FORMAT_DATE.format(temporal);
  }

  /**
   * 格式：HH:mm:ss
   */
  public static String formatTime(TemporalAccessor temporal) {
    return DateTimeConstant.FORMAT_TIME.format(temporal);
  }

  /**
   * 格式：yyyy-MM
   */
  public static String formatDateM(TemporalAccessor temporal) {
    return DateTimeConstant.FORMAT_DATE_M.format(temporal);
  }

  /**
   * 格式：HH:mm
   */
  public static String formatTimeM(TemporalAccessor temporal) {
    return DateTimeConstant.FORMAT_TIME_M.format(temporal);
  }

  /**
   * 格式：yyyyMMddHHmmssSSS
   */
  public static String formatYMDHMSS(TemporalAccessor temporal) {
    return DateTimeConstant.FORMAT_Y_M_D_H_M_S_S.format(temporal);
  }
  /**
   * 格式：yyyyMMddHHmmss
   */
  public static String formatYMDHMS(TemporalAccessor temporal) {
    return DateTimeConstant.FORMAT_Y_M_D_H_M_S.format(temporal);
  }
  /**
   * 格式：yyyyMMddHHmm
   */
  public static String formatYMDHM(TemporalAccessor temporal) {
    return DateTimeConstant.FORMAT_Y_M_D_H_M.format(temporal);
  }
  /**
   * 格式：yyyyMMdd
   */
  public static String formatYMD(TemporalAccessor temporal) {
    return DateTimeConstant.FORMAT_Y_M_D.format(temporal);
  }
  /**
   * 格式：yyyyMM
   */
  public static String formatYM(TemporalAccessor temporal) {
    return DateTimeConstant.FORMAT_Y_M.format(temporal);
  }
  /**
   * 格式：HHmmss
   */
  public static String formatHMS(TemporalAccessor temporal) {
    return DateTimeConstant.FORMAT_H_M_S.format(temporal);
  }
  /**
   * 格式：HHmm
   */
  public static String formatHM(TemporalAccessor temporal) {
    return DateTimeConstant.FORMAT_H_M.format(temporal);
  }
  /**
   * 格式：mmss
   */
  public static String formatMS(TemporalAccessor temporal) {
    return DateTimeConstant.FORMAT_M_S.format(temporal);
  }
  /**
   * 格式：yyMMdd
   */
  public static String formatY2MD(TemporalAccessor temporal) {
    return DateTimeConstant.FORMAT_Y2_M_D.format(temporal);
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
    return parseDateTime(dateStr, DateTimeConstant.FORMAT_DATETIME);
  }

  /**
   * 时间戳转成LocalDateTime(UTC)
   *
   * @author lvweijie
   * @date 2024/8/7 20:22
   * @param timestamp 时间戳: System.currentTimeMillis()
   * @return java.time.LocalDateTime
   */
  public static LocalDateTime parseDateTime(long timestamp) {
    return fromMilliseconds(timestamp);
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
  public static LocalDateTime parseDateTime(long timestamp, ZoneId zoneId) {
    return fromMilliseconds(timestamp, zoneId);
  }

  public static LocalDateTime parseDateTime(String dateTime, ZoneId zoneId) {
    return parseDateTime(dateTime, "", zoneId);
  }

  public static LocalDateTime parseDateTime(String dateTime, DateTimeFormatter formatter, ZoneId zoneId) {
    formatter = Optional.ofNullable(formatter).orElse(DateTimeConstant.FORMAT_DATETIME);
    LocalDateTime localDateTime = parseDateTime(dateTime, formatter);
    return toDateTime(localDateTime, zoneId);
  }

  public static LocalDateTime parseDateTime(String dateTime, String pattern, ZoneId zoneId) {
    DateTimeFormatter formatter = Func.isBlank(pattern) ? DateTimeConstant.FORMAT_DATETIME : DateTimeFormatter.ofPattern(pattern);
    return parseDateTime(dateTime, formatter, zoneId);
  }

  public static LocalDateTime parseUtcDateTime(String dateTime) {
    return parseDateTime(dateTime, "", ZoneId.of("UTC"));
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
    return parseDate(dateStr, DateTimeConstant.FORMAT_DATE);
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
    return parseTime(dateStr, DateTimeConstant.FORMAT_TIME);
  }

  /**
   * 时间转 Instant
   *
   * @param dateTime 时间
   * @return Instant
   */
  public static Instant toInstant(LocalDateTime dateTime, ZoneId zoneId) {
    zoneId = Optional.ofNullable(zoneId).orElse(ZoneId.systemDefault());
    return dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId).toInstant();
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
    zoneId = Optional.ofNullable(zoneId).orElse(ZoneId.systemDefault());
    return dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId).toLocalDateTime();
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
    zoneId = Optional.ofNullable(zoneId).orElse(ZoneId.systemDefault());
    return instant.atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId).toLocalDateTime();
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
   * 转换成java8 时间
   *
   * @param date Date
   * @return LocalDateTime
   */
  public static LocalDateTime fromDate(final Date date) {
    return fromDate(date, ZoneId.systemDefault());
  }

  public static LocalDateTime fromDate(final Date date, ZoneId zoneId) {
    zoneId = Optional.ofNullable(zoneId).orElse(ZoneId.systemDefault());
    return LocalDateTime.ofInstant(date.toInstant(), zoneId);
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

  /**
   * 比较两个日期，获取间隔天数
   *
   * @param startDate 开始日期
   * @param endDate   结束日期
   * @return 间隔天数
   */
  public static long betweenDays(LocalDate startDate, LocalDate endDate) {
    return ChronoUnit.DAYS.between(startDate, endDate);
  }

  public static Boolean inRange(LocalTime localTime, String hHmmStartTime, String hHmmEndTime) {
    LocalTime localStartTime = LocalTime.parse(hHmmStartTime);
    LocalTime localEndTime = LocalTime.parse(hHmmEndTime);
    return localTime.isAfter(localStartTime) && localTime.isBefore(localEndTime);
  }

  public static long diffSeconds(LocalDateTime startTime, LocalDateTime endTime) {
    long betweenMs = Duration.between(startTime, endTime).toMillis();
    return TimeUnit.MILLISECONDS.toSeconds(betweenMs);
  }

  public static long diffMinutes(LocalDateTime startTime, LocalDateTime endTime) {
    long betweenMs = Duration.between(startTime, endTime).toMillis();
    return TimeUnit.MILLISECONDS.toMinutes(betweenMs);
  }

  /**
   * localDateTime 转换成毫秒数
   *
   * @param localDateTime LocalDateTime
   * @return long
   */
  public static long toMilliseconds(final LocalDateTime localDateTime) {
    return toMilliseconds(localDateTime, ZoneId.systemDefault());
  }

  public static long toMilliseconds(final LocalDateTime localDateTime, ZoneId zoneId) {
    zoneId = Optional.ofNullable(zoneId).orElse(ZoneId.systemDefault());
    return localDateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId).toInstant().toEpochMilli();
  }

  /**
   * localDate 转换成毫秒数
   *
   * @param localDate LocalDate
   * @return long
   */
  public static long toMilliseconds(LocalDate localDate) {
    return toMilliseconds(localDate.atStartOfDay());
  }

  /**
   * 转换成java8 时间
   *
   * @param milliseconds 毫秒数
   * @return LocalDateTime
   */
  public static LocalDateTime fromMilliseconds(final long milliseconds) {
    return fromMilliseconds(milliseconds, ZoneId.systemDefault());
  }

  /**
   * 转换成java8 时间
   *
   * @param milliseconds 毫秒数
   * @return LocalDateTime
   */
  public static LocalDateTime fromMilliseconds(final long milliseconds, ZoneId zoneId) {
    zoneId = Optional.ofNullable(zoneId).orElse(ZoneId.systemDefault());
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), zoneId);
  }

  /**
   * 获取今年起始时间
   */
  public static LocalDateTime getYearStart() {
    return LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()), LocalTime.MIN);
  }

  /**
   * 获取今年结束时间
   */
  public static LocalDateTime getYearEnd() {
    return LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.lastDayOfYear()), LocalTime.MAX);
  }

  /**
   * 获取当季度起始时间
   */
  public static LocalDateTime getQuarterStart() {
    LocalDate now = LocalDate.now();
    Month month = Month.of(now.getMonth().firstMonthOfQuarter().getValue());
    return LocalDateTime.of(LocalDate.of(now.getYear(), month, 1), LocalTime.MIN);
  }

  /**
   * 获取当季度结束时间
   */
  public static LocalDateTime getQuarterEnd() {
    LocalDate now = LocalDate.now();
    Month month = Month.of(now.getMonth().firstMonthOfQuarter().getValue()).plus(2L);
    return LocalDateTime.of(LocalDate.of(now.getYear(), month, month.maxLength()), LocalTime.MAX);
  }

  /**
   * 获取当月起始时间
   *
   * @return Date
   */
  public static Date getMonthStart() {
    return toDate(LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIN));
  }

  /**
   * 获取当月结束时间
   */
  public static LocalDateTime getMonthEnd() {
    return LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX);
  }

  /**
   * 获取当周起始时间
   */
  public static LocalDateTime getWeekStart() {
    return LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)), LocalTime.MIN);
  }

  /**
   * 获取当周结束时间
   */
  public static LocalDateTime getWeekEnd() {
    return LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)), LocalTime.MAX);
  }

  /**
   * 获取当天起始时间00:00:00.000
   */
  public static LocalDateTime getDayStart() {
    return LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
  }

  /**
   * 获取当天结束时间23:59:59.999
   */
  public static LocalDateTime getDayEnd() {
    return LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
  }

  /**
   * 获取当天中午时间12:00:00
   */
  public static LocalDateTime getDayNoon() {
    return LocalDateTime.of(LocalDate.now(), LocalTime.NOON);
  }
}
