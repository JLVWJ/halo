package com.lvwj.halo.common.utils;

import com.lvwj.halo.common.constants.DateTimeConstant;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;
import java.util.*;

/**
 * Date工具类
 */
public class DateUtil {

	/**
	 * 获取当前日期
	 *
	 * @return 当前日期
	 */
	public static Date now() {
		return new Date();
	}

	/**
	 * 添加年
	 *
	 * @param date       时间
	 * @param yearsToAdd 添加的年数
	 * @return 设置后的时间
	 */
	public static Date plusYears(Date date, int yearsToAdd) {
		return set(date, Calendar.YEAR, yearsToAdd);
	}

	/**
	 * 添加月
	 *
	 * @param date        时间
	 * @param monthsToAdd 添加的月数
	 * @return 设置后的时间
	 */
	public static Date plusMonths(Date date, int monthsToAdd) {
		return set(date, Calendar.MONTH, monthsToAdd);
	}

	/**
	 * 添加周
	 *
	 * @param date       时间
	 * @param weeksToAdd 添加的周数
	 * @return 设置后的时间
	 */
	public static Date plusWeeks(Date date, int weeksToAdd) {
		return plus(date, Period.ofWeeks(weeksToAdd));
	}

	/**
	 * 添加天
	 *
	 * @param date      时间
	 * @param daysToAdd 添加的天数
	 * @return 设置后的时间
	 */
	public static Date plusDays(Date date, long daysToAdd) {
		return plus(date, Duration.ofDays(daysToAdd));
	}

	/**
	 * 添加小时
	 *
	 * @param date       时间
	 * @param hoursToAdd 添加的小时数
	 * @return 设置后的时间
	 */
	public static Date plusHours(Date date, long hoursToAdd) {
		return plus(date, Duration.ofHours(hoursToAdd));
	}

	/**
	 * 添加分钟
	 *
	 * @param date         时间
	 * @param minutesToAdd 添加的分钟数
	 * @return 设置后的时间
	 */
	public static Date plusMinutes(Date date, long minutesToAdd) {
		return plus(date, Duration.ofMinutes(minutesToAdd));
	}

	/**
	 * 添加秒
	 *
	 * @param date         时间
	 * @param secondsToAdd 添加的秒数
	 * @return 设置后的时间
	 */
	public static Date plusSeconds(Date date, long secondsToAdd) {
		return plus(date, Duration.ofSeconds(secondsToAdd));
	}

	/**
	 * 添加毫秒
	 *
	 * @param date        时间
	 * @param millisToAdd 添加的毫秒数
	 * @return 设置后的时间
	 */
	public static Date plusMillis(Date date, long millisToAdd) {
		return plus(date, Duration.ofMillis(millisToAdd));
	}

	/**
	 * 添加纳秒
	 *
	 * @param date       时间
	 * @param nanosToAdd 添加的纳秒数
	 * @return 设置后的时间
	 */
	public static Date plusNanos(Date date, long nanosToAdd) {
		return plus(date, Duration.ofNanos(nanosToAdd));
	}

	/**
	 * 日期添加时间量
	 *
	 * @param date   时间
	 * @param amount 时间量
	 * @return 设置后的时间
	 */
	public static Date plus(Date date, TemporalAmount amount) {
		Instant instant = date.toInstant();
		return Date.from(instant.plus(amount));
	}

	/**
	 * 减少年
	 *
	 * @param date  时间
	 * @param years 减少的年数
	 * @return 设置后的时间
	 */
	public static Date minusYears(Date date, int years) {
		return set(date, Calendar.YEAR, -years);
	}

	/**
	 * 减少月
	 *
	 * @param date   时间
	 * @param months 减少的月数
	 * @return 设置后的时间
	 */
	public static Date minusMonths(Date date, int months) {
		return set(date, Calendar.MONTH, -months);
	}

	/**
	 * 减少周
	 *
	 * @param date  时间
	 * @param weeks 减少的周数
	 * @return 设置后的时间
	 */
	public static Date minusWeeks(Date date, int weeks) {
		return minus(date, Period.ofWeeks(weeks));
	}

	/**
	 * 减少天
	 *
	 * @param date 时间
	 * @param days 减少的天数
	 * @return 设置后的时间
	 */
	public static Date minusDays(Date date, long days) {
		return minus(date, Duration.ofDays(days));
	}

	/**
	 * 减少小时
	 *
	 * @param date  时间
	 * @param hours 减少的小时数
	 * @return 设置后的时间
	 */
	public static Date minusHours(Date date, long hours) {
		return minus(date, Duration.ofHours(hours));
	}

	/**
	 * 减少分钟
	 *
	 * @param date    时间
	 * @param minutes 减少的分钟数
	 * @return 设置后的时间
	 */
	public static Date minusMinutes(Date date, long minutes) {
		return minus(date, Duration.ofMinutes(minutes));
	}

	/**
	 * 减少秒
	 *
	 * @param date    时间
	 * @param seconds 减少的秒数
	 * @return 设置后的时间
	 */
	public static Date minusSeconds(Date date, long seconds) {
		return minus(date, Duration.ofSeconds(seconds));
	}

	/**
	 * 减少毫秒
	 *
	 * @param date   时间
	 * @param millis 减少的毫秒数
	 * @return 设置后的时间
	 */
	public static Date minusMillis(Date date, long millis) {
		return minus(date, Duration.ofMillis(millis));
	}

	/**
	 * 减少纳秒
	 *
	 * @param date  时间
	 * @param nanos 减少的纳秒数
	 * @return 设置后的时间
	 */
	public static Date minusNanos(Date date, long nanos) {
		return minus(date, Duration.ofNanos(nanos));
	}

	/**
	 * 日期减少时间量
	 *
	 * @param date   时间
	 * @param amount 时间量
	 * @return 设置后的时间
	 */
	public static Date minus(Date date, TemporalAmount amount) {
		Instant instant = date.toInstant();
		return Date.from(instant.minus(amount));
	}

	/**
	 * 设置日期属性
	 *
	 * @param date          时间
	 * @param calendarField 更改的属性
	 * @param amount        更改数，-1表示减少
	 * @return 设置后的时间
	 */
	private static Date set(Date date, int calendarField, int amount) {
		Assert.notNull(date, "The date must not be null");
		Calendar c = Calendar.getInstance();
		c.setLenient(false);
		c.setTime(date);
		c.add(calendarField, amount);
		return c.getTime();
	}

	/**
	 * 日期时间格式化
	 *
	 * @param date 时间
	 * @return 格式化后的时间
	 */
	public static String formatDateTime(Date date) {
		return DateTimeConstant.DATETIME_FORMAT.format(date);
	}

	/**
	 * 日期时间格式化
	 *
	 * @param date 时间
	 * @return 格式化后的时间
	 */
	public static String formatDateTimeMini(Date date) {
		return DateTimeConstant.DATETIME_MINI_FORMAT.format(date);
	}

	/**
	 * 日期时间格式化
	 *
	 * @param date 时间
	 * @return 格式化后的时间
	 */
	public static String formatDateTimeM(Date date) {
		return DateTimeConstant.DATETIME_M_FORMAT.format(date);
	}

	/**
	 * 日期时间格式化
	 *
	 * @param date 时间
	 * @return 格式化后的时间
	 */
	public static String formatDateTimeMMini(Date date) {
		return DateTimeConstant.DATETIME_M_MINI_FORMAT.format(date);
	}

	/**
	 * 日期格式化
	 *
	 * @param date 时间
	 * @return 格式化后的时间
	 */
	public static String formatDate(Date date) {
		return DateTimeConstant.DATE_FORMAT.format(date);
	}

	/**
	 * 日期格式化
	 *
	 * @param date 时间
	 * @return 格式化后的时间
	 */
	public static String formatDateMini(Date date) {
		return DateTimeConstant.DATE_MINI_FORMAT.format(date);
	}

	/**
	 * 日期格式化
	 *
	 * @param date 时间
	 * @return 格式化后的时间
	 */
	public static String formatDateM(Date date) {
		return DateTimeConstant.DATE_M_FORMAT.format(date);
	}

	/**
	 * 日期格式化
	 *
	 * @param date 时间
	 * @return 格式化后的时间
	 */
	public static String formatDateMMini(Date date) {
		return DateTimeConstant.DATE_M_MINI_FORMAT.format(date);
	}

	/**
	 * 时间格式化
	 *
	 * @param date 时间
	 * @return 格式化后的时间
	 */
	public static String formatTime(Date date) {
		return DateTimeConstant.TIME_FORMAT.format(date);
	}

	/**
	 * 时间格式化
	 *
	 * @param date 时间
	 * @return 格式化后的时间
	 */
	public static String formatTimeMini(Date date) {
		return DateTimeConstant.TIME_MINI_FORMAT.format(date);
	}

	/**
	 * 时间格式化
	 *
	 * @param date 时间
	 * @return 格式化后的时间
	 */
	public static String formatTimeM(Date date) {
		return DateTimeConstant.TIME_M_FORMAT.format(date);
	}

	/**
	 * 时间格式化
	 *
	 * @param date 时间
	 * @return 格式化后的时间
	 */
	public static String formatTimeMMini(Date date) {
		return DateTimeConstant.TIME_M_MINI_FORMAT.format(date);
	}

	/**
	 * 日期格式化
	 *
	 * @param date    时间
	 * @param pattern 表达式
	 * @return 格式化后的时间
	 */
	public static String format(Date date, String pattern) {
		return ConcurrentDateFormat.of(pattern).format(date);
	}

	/**
	 * 将字符串转换为时间
	 *
	 * @param dateStr 时间字符串
	 * @param pattern 表达式
	 * @return 时间
	 */
	public static Date parse(String dateStr, String pattern) {
		ConcurrentDateFormat format = ConcurrentDateFormat.of(pattern);
		try {
			return format.parse(dateStr);
		} catch (ParseException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将字符串转换为时间
	 *
	 * @param dateStr 时间字符串
	 * @param format  ConcurrentDateFormat
	 * @return 时间
	 */
	public static Date parse(String dateStr, ConcurrentDateFormat format) {
		try {
			return format.parse(dateStr);
		} catch (ParseException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将字符串转换为时间
	 *
	 * @param dateStr 时间字符串
	 * @param pattern 表达式
	 * @return 时间
	 */
	public static <T> T parse(String dateStr, String pattern, TemporalQuery<T> query) {
		return DateTimeFormatter.ofPattern(pattern).parse(dateStr, query);
	}

	/**
	 * 时间转 Instant
	 *
	 * @param dateTime 时间
	 * @return Instant
	 */
	public static Instant toInstant(LocalDateTime dateTime) {
		return toInstant(dateTime, ZoneId.systemDefault());
	}

	public static Instant toInstant(LocalDateTime dateTime, ZoneId zoneId) {
		zoneId = Optional.ofNullable(zoneId).orElse(ZoneId.systemDefault());
		return dateTime.atZone(zoneId).toInstant();
	}

	/**
	 * Instant 转 时间
	 *
	 * @param instant Instant
	 * @return Instant
	 */
	public static LocalDateTime toDateTime(Instant instant) {
		return toDateTime(instant, ZoneId.systemDefault());
	}

	public static LocalDateTime toDateTime(Instant instant, ZoneId zoneId) {
		zoneId = Optional.ofNullable(zoneId).orElse(ZoneId.systemDefault());
		return LocalDateTime.ofInstant(instant, zoneId);
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

	/**
	 * 转换成 date
	 *
	 * @param localDate LocalDate
	 * @return Date
	 */
	public static Date toDate(final LocalDate localDate) {
		return toDate(localDate, ZoneId.systemDefault());
	}

	public static Date toDate(final LocalDate localDate, ZoneId zoneId) {
		zoneId = Optional.ofNullable(zoneId).orElse(ZoneId.systemDefault());
		return Date.from(localDate.atStartOfDay(zoneId).toInstant());
	}

	/**
	 * Converts local date time to Calendar.
	 */
	public static Calendar toCalendar(final LocalDateTime localDateTime) {
		return toCalendar(localDateTime, ZoneId.systemDefault());
	}

	public static Calendar toCalendar(final LocalDateTime localDateTime, ZoneId zoneId) {
		zoneId = Optional.ofNullable(zoneId).orElse(ZoneId.systemDefault());
		return GregorianCalendar.from(ZonedDateTime.of(localDateTime, zoneId));
	}

	/**
	 * localDateTime 转换成毫秒数
	 *
	 * @param localDateTime LocalDateTime
	 * @return long
	 */
	public static long toMilliseconds(final LocalDateTime localDateTime) {
		return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
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
	 * @param calendar 日历
	 * @return LocalDateTime
	 */
	public static LocalDateTime fromCalendar(final Calendar calendar) {
		TimeZone tz = calendar.getTimeZone();
		ZoneId zid = tz == null ? ZoneId.systemDefault() : tz.toZoneId();
		return LocalDateTime.ofInstant(calendar.toInstant(), zid);
	}

	/**
	 * 转换成java8 时间
	 *
	 * @param instant Instant
	 * @return LocalDateTime
	 */
	public static LocalDateTime fromInstant(final Instant instant) {
		return fromInstant(instant, ZoneId.systemDefault());
	}

	public static LocalDateTime fromInstant(final Instant instant, ZoneId zoneId) {
		zoneId = Optional.ofNullable(zoneId).orElse(ZoneId.systemDefault());
		return LocalDateTime.ofInstant(instant, zoneId);
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
	 * 比较2个 时间差
	 *
	 * @param startDate 开始时间
	 * @param endDate   结束时间
	 * @return 时间间隔
	 */
	public static Duration between(Date startDate, Date endDate) {
		return Duration.between(startDate.toInstant(), endDate.toInstant());
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

	/**
	 * 比较两个日期，获取间隔天数
	 *
	 * @param startDate 开始日期
	 * @param endDate   结束日期
	 * @return 间隔天数
	 */
	public static long betweenDays(Date startDate, Date endDate) {
		return ChronoUnit.DAYS.between(startDate.toInstant(), endDate.toInstant());
	}

	/**
	 * 将秒数转换为日时分秒
	 *
	 * @param second 秒数
	 * @return 时间
	 */
	public static String secondToTime(Long second) {
		// 判断是否为空
		if (second == null || second == 0L) {
			return StringPool.EMPTY;
		}
		//转换天数
		long days = second / 86400;
		//剩余秒数
		second = second % 86400;
		//转换小时
		long hours = second / 3600;
		//剩余秒数
		second = second % 3600;
		//转换分钟
		long minutes = second / 60;
		//剩余秒数
		second = second % 60;
		if (days > 0) {
			return StringUtil.format("{}天{}小时{}分{}秒", days, hours, minutes, second);
		} else {
			return StringUtil.format("{}小时{}分{}秒", hours, minutes, second);
		}
	}

	/**
	 * 获取今天的日期
	 *
	 * @return 时间
	 */
	public static String today() {
		return format(new Date(), DateTimeConstant.PATTERN_Y_M_D);
	}

	/**
	 * 获取今天的时间
	 *
	 * @return 时间
	 */
	public static String time() {
		return format(new Date(), DateTimeConstant.PATTERN_Y_M_D_H_M_S);
	}

	/**
	 * 获取今天的小时数
	 *
	 * @return 时间
	 */
	public static Integer hour() {
		return NumberUtil.toInt(format(new Date(), DateTimeConstant.PATTERN_H));
	}

	/**
	 * 获取今年起始时间
	 *
	 * @return Date
	 */
	public static Date getYearStart() {
		return toDate(LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()), LocalTime.MIN));
	}

	/**
	 * 获取今年结束时间
	 *
	 * @return Date
	 */
	public static Date getYearEnd() {
		return toDate(LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.lastDayOfYear()), LocalTime.MAX));
	}

	/**
	 * 获取当季度起始时间
	 *
	 * @return Date
	 */
	public static Date getQuarterStart() {
		LocalDate now = LocalDate.now();
		Month month = Month.of(now.getMonth().firstMonthOfQuarter().getValue());
		return toDate(LocalDateTime.of(LocalDate.of(now.getYear(), month, 1), LocalTime.MIN));
	}

	/**
	 * 获取当季度结束时间
	 *
	 * @return Date
	 */
	public static Date getQuarterEnd() {
		LocalDate now = LocalDate.now();
		Month month = Month.of(now.getMonth().firstMonthOfQuarter().getValue()).plus(2L);
		return toDate(LocalDateTime.of(LocalDate.of(now.getYear(), month, month.maxLength()), LocalTime.MAX));
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
	 *
	 * @return Date
	 */
	public static Date getMonthEnd() {
		return toDate(LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX));
	}

	/**
	 * 获取当周起始时间
	 *
	 * @return Date
	 */
	public static Date getWeekStart() {
		return toDate(LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)), LocalTime.MIN));
	}

	/**
	 * 获取当周结束时间
	 *
	 * @return Date
	 */
	public static Date getWeekEnd() {
		return toDate(LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)), LocalTime.MAX));
	}

	/**
	 * 获取当天起始时间00:00:00
	 *
	 * @return Date
	 */
	public static Date getDayStart() {
		return toDate(LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
	}

	/**
	 * 获取当天中午时间12:00:00
	 *
	 * @return Date
	 */
	public static Date getDayNoon() {
		return toDate(LocalDateTime.of(LocalDate.now(), LocalTime.NOON));
	}

	/**
	 * 获取当天结束时间23:59:59
	 *
	 * @return Date
	 */
	public static Date getDayEnd() {
		return toDate(LocalDateTime.of(LocalDate.now(), LocalTime.MAX));
	}

	/**
	 * 获取指定日期的起始时间
	 *
	 * @return Date
	 */
	public static Date getDayStart(Date date) {
		Assert.notNull(date, "The date must not be null");
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	/**
	 * 获取指定日期的结束时间
	 *
	 * @return Date
	 */
	public static Date getDayEnd(Date date) {
		Assert.notNull(date, "The date must not be null");
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime();
	}
}
