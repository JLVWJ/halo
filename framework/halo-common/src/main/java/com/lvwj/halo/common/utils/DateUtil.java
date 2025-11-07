package com.lvwj.halo.common.utils;

import org.springframework.util.Assert;

import java.text.ParseException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

/**
 * Date工具类
 */
public class DateUtil {

	// ========================== 一、Date 基础操作 ==========================

	/**
	 * 获取当前日期
	 *
	 * @return 当前日期
	 */
	public static Date now() {
		return new Date();
	}

	/**
	 * Date 增加年
	 *
	 * @param date       目标时间
	 * @param yearsToAdd 增加的年数（负数表示减少）
	 * @return 操作后的 Date
	 */
	public static Date plusYears(Date date, int yearsToAdd) {
		Assert.notNull(date, "The date must not be null");
		return convertAndOperate(date, dt -> DateTimeUtil.plusYears(dt, yearsToAdd));
	}

	/**
	 * Date 增加月
	 *
	 * @param date        目标时间
	 * @param monthsToAdd 增加的月数（负数表示减少）
	 * @return 操作后的 Date
	 */
	public static Date plusMonths(Date date, int monthsToAdd) {
		Assert.notNull(date, "The date must not be null");
		return convertAndOperate(date, dt -> DateTimeUtil.plusMonths(dt, monthsToAdd));
	}

	/**
	 * Date 增加周
	 *
	 * @param date       目标时间
	 * @param weeksToAdd 增加的周数（负数表示减少）
	 * @return 操作后的 Date
	 */
	public static Date plusWeeks(Date date, int weeksToAdd) {
		Assert.notNull(date, "The date must not be null");
		return plusDays(date, weeksToAdd * 7L);
	}

	/**
	 * Date 增加天
	 *
	 * @param date      目标时间
	 * @param daysToAdd 增加的天数（负数表示减少）
	 * @return 操作后的 Date
	 */
	public static Date plusDays(Date date, long daysToAdd) {
		Assert.notNull(date, "The date must not be null");
		return convertAndOperate(date, dt -> DateTimeUtil.plusDays(dt, Math.toIntExact(daysToAdd)));
	}

	/**
	 * Date 增加小时
	 *
	 * @param date       目标时间
	 * @param hoursToAdd 增加的小时数（负数表示减少）
	 * @return 操作后的 Date
	 */
	public static Date plusHours(Date date, long hoursToAdd) {
		Assert.notNull(date, "The date must not be null");
		return convertAndOperate(date, dt -> DateTimeUtil.plusHours(dt, hoursToAdd));
	}

	/**
	 * Date 增加分钟
	 *
	 * @param date         目标时间
	 * @param minutesToAdd 增加的分钟数（负数表示减少）
	 * @return 操作后的 Date
	 */
	public static Date plusMinutes(Date date, long minutesToAdd) {
		Assert.notNull(date, "The date must not be null");
		return convertAndOperate(date, dt -> DateTimeUtil.plusMinutes(dt, minutesToAdd));
	}

	/**
	 * Date 增加秒
	 *
	 * @param date         目标时间
	 * @param secondsToAdd 增加的秒数（负数表示减少）
	 * @return 操作后的 Date
	 */
	public static Date plusSeconds(Date date, long secondsToAdd) {
		Assert.notNull(date, "The date must not be null");
		LocalDateTime dt = DateTimeUtil.toDateTime(date);
		dt = dt.plusSeconds(secondsToAdd);
		return DateTimeUtil.toDate(dt);
	}

	/**
	 * Date 增加毫秒
	 *
	 * @param date        目标时间
	 * @param millisToAdd 增加的毫秒数（负数表示减少）
	 * @return 操作后的 Date
	 */
	public static Date plusMillis(Date date, long millisToAdd) {
		Assert.notNull(date, "The date must not be null");
		LocalDateTime dt = DateTimeUtil.toDateTime(date);
		dt = dt.plusNanos(millisToAdd * 1_000_000);
		return DateTimeUtil.toDate(dt);
	}

	/**
	 * Date 增加纳秒
	 *
	 * @param date       目标时间
	 * @param nanosToAdd 增加的纳秒数（负数表示减少）
	 * @return 操作后的 Date
	 */
	public static Date plusNanos(Date date, long nanosToAdd) {
		Assert.notNull(date, "The date must not be null");
		LocalDateTime dt = DateTimeUtil.toDateTime(date);
		dt = dt.plusNanos(nanosToAdd);
		return DateTimeUtil.toDate(dt);
	}

	/**
	 * Date 减少年
	 *
	 * @param date  目标时间
	 * @param years 减少的年数
	 * @return 操作后的 Date
	 */
	public static Date minusYears(Date date, int years) {
		return plusYears(date, -years);
	}

	/**
	 * Date 减少月
	 *
	 * @param date   目标时间
	 * @param months 减少的月数
	 * @return 操作后的 Date
	 */
	public static Date minusMonths(Date date, int months) {
		return plusMonths(date, -months);
	}

	/**
	 * Date 减少周
	 *
	 * @param date  目标时间
	 * @param weeks 减少的周数
	 * @return 操作后的 Date
	 */
	public static Date minusWeeks(Date date, int weeks) {
		return plusWeeks(date, -weeks);
	}

	/**
	 * Date 减少天
	 *
	 * @param date 目标时间
	 * @param days 减少的天数
	 * @return 操作后的 Date
	 */
	public static Date minusDays(Date date, long days) {
		return plusDays(date, -days);
	}

	/**
	 * Date 减少小时
	 *
	 * @param date  目标时间
	 * @param hours 减少的小时数
	 * @return 操作后的 Date
	 */
	public static Date minusHours(Date date, long hours) {
		return plusHours(date, -hours);
	}

	/**
	 * Date 减少分钟
	 *
	 * @param date    目标时间
	 * @param minutes 减少的分钟数
	 * @return 操作后的 Date
	 */
	public static Date minusMinutes(Date date, long minutes) {
		return plusMinutes(date, -minutes);
	}

	/**
	 * Date 减少秒
	 *
	 * @param date    目标时间
	 * @param seconds 减少的秒数
	 * @return 操作后的 Date
	 */
	public static Date minusSeconds(Date date, long seconds) {
		return plusSeconds(date, -seconds);
	}

	/**
	 * Date 减少毫秒
	 *
	 * @param date   目标时间
	 * @param millis 减少的毫秒数
	 * @return 操作后的 Date
	 */
	public static Date minusMillis(Date date, long millis) {
		return plusMillis(date, -millis);
	}

	/**
	 * Date 减少纳秒
	 *
	 * @param date  目标时间
	 * @param nanos 减少的纳秒数
	 * @return 操作后的 Date
	 */
	public static Date minusNanos(Date date, long nanos) {
		return plusNanos(date, -nanos);
	}


	// ========================== 二、Date 格式化 ==========================

	/**
	 * Date 格式化：yyyy-MM-dd HH:mm:ss
	 *
	 * @param date 目标时间
	 * @return 格式化字符串
	 */
	public static String formatDateTime(Date date) {
		return DateTimeUtil.formatDateTime(DateTimeUtil.toDateTime(date));
	}

	/**
	 * Date 格式化：紧凑日期时间（如 yyyyMMddHHmmss）
	 *
	 * @param date 目标时间
	 * @return 格式化字符串
	 */
	public static String formatDateTimeMini(Date date) {
		return DateTimeUtil.formatYMDHMS(DateTimeUtil.toDateTime(date));
	}

	/**
	 * Date 格式化：yyyy-MM-dd HH:mm
	 *
	 * @param date 目标时间
	 * @return 格式化字符串
	 */
	public static String formatDateTimeM(Date date) {
		return DateTimeUtil.formatDateTimeM(DateTimeUtil.toDateTime(date));
	}

	/**
	 * Date 格式化：紧凑日期时间（yyyyMMddHHmm）
	 *
	 * @param date 目标时间
	 * @return 格式化字符串
	 */
	public static String formatDateTimeMMini(Date date) {
		return DateTimeUtil.formatYMDHM(DateTimeUtil.toDateTime(date));
	}

	/**
	 * Date 格式化：yyyy-MM-dd
	 *
	 * @param date 目标时间
	 * @return 格式化字符串
	 */
	public static String formatDate(Date date) {
		return DateTimeUtil.formatDate(DateTimeUtil.toDateTime(date));
	}

	/**
	 * Date 格式化：紧凑日期（yyyyMMdd）
	 *
	 * @param date 目标时间
	 * @return 格式化字符串
	 */
	public static String formatDateMini(Date date) {
		return DateTimeUtil.formatYMD(DateTimeUtil.toDateTime(date));
	}

	/**
	 * Date 格式化：yyyy-MM
	 *
	 * @param date 目标时间
	 * @return 格式化字符串
	 */
	public static String formatDateM(Date date) {
		return DateTimeUtil.formatDateM(DateTimeUtil.toDateTime(date));
	}

	/**
	 * Date 格式化：紧凑年月（yyyyMM）
	 *
	 * @param date 目标时间
	 * @return 格式化字符串
	 */
	public static String formatDateMMini(Date date) {
		return DateTimeUtil.formatYM(DateTimeUtil.toDateTime(date));
	}

	/**
	 * Date 格式化：HH:mm:ss
	 *
	 * @param date 目标时间
	 * @return 格式化字符串
	 */
	public static String formatTime(Date date) {
		return DateTimeUtil.formatTime(DateTimeUtil.toDateTime(date));
	}

	/**
	 * Date 格式化：紧凑时间（HHmmss）
	 *
	 * @param date 目标时间
	 * @return 格式化字符串
	 */
	public static String formatTimeMini(Date date) {
		return DateTimeUtil.formatHMS(DateTimeUtil.toDateTime(date));
	}

	/**
	 * Date 格式化：HH:mm
	 *
	 * @param date 目标时间
	 * @return 格式化字符串
	 */
	public static String formatTimeM(Date date) {
		return DateTimeUtil.formatTimeM(DateTimeUtil.toDateTime(date));
	}

	/**
	 * Date 格式化：紧凑时分（HHmm）
	 *
	 * @param date 目标时间
	 * @return 格式化字符串
	 */
	public static String formatTimeMMini(Date date) {
		return DateTimeUtil.formatHM(DateTimeUtil.toDateTime(date));
	}

	/**
	 * Date 自定义格式化
	 *
	 * @param date    目标时间
	 * @param pattern 格式表达式
	 * @return 格式化字符串
	 */
	public static String format(Date date, String pattern) {
		return DateTimeUtil.format(DateTimeUtil.toDateTime(date), pattern);
	}


	// ========================== 三、Date 解析 ==========================

	/**
	 * 字符串解析为 Date（指定格式）
	 *
	 * @param dateStr 时间字符串
	 * @param pattern 格式表达式
	 * @return 解析后的 Date
	 */
	public static Date parse(String dateStr, String pattern) {
		LocalDateTime dt = DateTimeUtil.parseDateTime(dateStr, pattern);
		return DateTimeUtil.toDate(dt);
	}

	/**
	 * 字符串解析为 Date（指定 ConcurrentDateFormat）
	 *
	 * @param dateStr 时间字符串
	 * @param format  格式化器
	 * @return 解析后的 Date
	 */
	public static Date parse(String dateStr, ConcurrentDateFormat format) {
		try {
			return format.parse(dateStr);
		} catch (ParseException e) {
			throw Exceptions.unchecked(e);
		}
	}


	// ========================== 四、类型转换 ==========================

	/**
	 * LocalDateTime 转换为 Calendar
	 *
	 * @param localDateTime 目标时间
	 * @return Calendar
	 */
	public static Calendar toCalendar(final LocalDateTime localDateTime) {
		return toCalendar(localDateTime, ZoneId.systemDefault());
	}

	/**
	 * LocalDateTime 转换为 Calendar（指定时区）
	 *
	 * @param localDateTime 目标时间
	 * @param zoneId        时区
	 * @return Calendar
	 */
	public static Calendar toCalendar(final LocalDateTime localDateTime, ZoneId zoneId) {
		zoneId = Optional.ofNullable(zoneId).orElse(ZoneId.systemDefault());
		return GregorianCalendar.from(ZonedDateTime.of(localDateTime, zoneId));
	}

	/**
	 * Date 转换为 Calendar
	 *
	 * @param date 目标时间
	 * @return Calendar
	 */
	public static Calendar toCalendar(final Date date) {
		Assert.notNull(date, "The date must not be null");
		LocalDateTime dt = DateTimeUtil.toDateTime(date);
		return toCalendar(dt);
	}

	/**
	 * Calendar 转换为 Date
	 *
	 * @param calendar 目标日历
	 * @return Date
	 */
	public static Date fromCalendar(final Calendar calendar) {
		Assert.notNull(calendar, "The calendar must not be null");
		LocalDateTime dt = DateTimeUtil.toDateTime(calendar.getTime());
		return DateTimeUtil.toDate(dt);
	}

	// ========================== 五、时间计算与比较 ==========================

	/**
	 * 比较两个Date的间隔年数（基于日历，依赖系统时区）
	 * <p>需转换为带时区的日期计算（如2023-12-31到2024-01-01算1年）</p>
	 *
	 * @param startDate 开始时间（非空）
	 * @param endDate   结束时间（非空）
	 * @return 间隔年数（endDate - startDate的完整年份，可为负数）
	 * @throws NullPointerException 若startDate或endDate为null
	 * @example ① 2020-01-01 与 2024-10-24 → 4年
	 * @example ② 2024-02-28 与 2024-03-01 → 0年（不足1年）
	 */
	public static long betweenYears(Date startDate, Date endDate) {
		Objects.requireNonNull(startDate, "开始时间[startDate]不能为null");
		Objects.requireNonNull(endDate, "结束时间[endDate]不能为null");

		// 转换为系统时区的LocalDate（年月计算依赖时区）
		LocalDate start = toLocalDate(startDate);
		LocalDate end = toLocalDate(endDate);
		return ChronoUnit.YEARS.between(start, end);
	}

	/**
	 * 比较两个Date的间隔月数（基于日历，依赖系统时区）
	 * <p>需转换为带时区的日期计算（如2024-01-15到2024-02-14算0个月，到2024-02-15算1个月）</p>
	 *
	 * @param startDate 开始时间（非空）
	 * @param endDate   结束时间（非空）
	 * @return 间隔月数（endDate - startDate的完整月份，可为负数）
	 * @throws NullPointerException 若startDate或endDate为null
	 * @example ① 2024-01-01 与 2024-04-01 → 3个月
	 * @example ② 2024-04-01 与 2024-04-30 → 0个月（不足1个月）
	 */
	public static long betweenMonths(Date startDate, Date endDate) {
		Objects.requireNonNull(startDate, "开始时间[startDate]不能为null");
		Objects.requireNonNull(endDate, "结束时间[endDate]不能为null");

		LocalDate start = toLocalDate(startDate);
		LocalDate end = toLocalDate(endDate);
		return ChronoUnit.MONTHS.between(start, end);
	}

	/**
	 * 比较两个Date的间隔天数（基于日历，依赖系统时区，忽略时间部分）
	 * <p>如2024-10-24 23:59到2024-10-25 00:01算1天（日历日期差）</p>
	 *
	 * @param startDate 开始时间（非空）
	 * @param endDate   结束时间（非空）
	 * @return 间隔天数（endDate - startDate的日历天数，可为负数）
	 * @throws NullPointerException 若startDate或endDate为null
	 * @example ① 2024-10-24 与 2024-10-26 → 2天
	 * @example ② 2024-10-25 00:00 与 2024-10-24 23:59 → -1天
	 */
	public static long betweenDays(Date startDate, Date endDate) {
		Objects.requireNonNull(startDate, "开始时间[startDate]不能为null");
		Objects.requireNonNull(endDate, "结束时间[endDate]不能为null");

		LocalDate start = toLocalDate(startDate);
		LocalDate end = toLocalDate(endDate);
		return ChronoUnit.DAYS.between(start, end);
	}

	/**
	 * 比较两个Date的间隔小时数（绝对时间差，每3600秒为1小时）
	 * <p>不依赖时区，基于时间戳计算（如2小时59分59秒 → 2小时）</p>
	 *
	 * @param startDate 开始时间（非空）
	 * @param endDate   结束时间（非空）
	 * @return 间隔小时数（endDate - startDate的完整小时，可为负数）
	 * @throws NullPointerException 若startDate或endDate为null
	 * @example ① 10:00:00 与 13:30:00 → 3小时（3.5小时取整）
	 * @example ② 23:00:00 与 次日01:00:00 → 2小时
	 */
	public static long betweenHours(Date startDate, Date endDate) {
		Objects.requireNonNull(startDate, "开始时间[startDate]不能为null");
		Objects.requireNonNull(endDate, "结束时间[endDate]不能为null");

		Instant start = startDate.toInstant();
		Instant end = endDate.toInstant();
		return ChronoUnit.HOURS.between(start, end);
	}

	/**
	 * 比较两个Date的间隔分钟数（绝对时间差，每60秒为1分钟）
	 * <p>不依赖时区，基于时间戳计算（如1分钟59秒 → 1分钟）</p>
	 *
	 * @param startDate 开始时间（非空）
	 * @param endDate   结束时间（非空）
	 * @return 间隔分钟数（endDate - startDate的完整分钟，可为负数）
	 * @throws NullPointerException 若startDate或endDate为null
	 * @example ① 10:00:00 与 10:25:30 → 25分钟（25.5分钟取整）
	 * @example ② 10:59:30 与 11:00:20 → 0分钟（不足1分钟）
	 */
	public static long betweenMinutes(Date startDate, Date endDate) {
		Objects.requireNonNull(startDate, "开始时间[startDate]不能为null");
		Objects.requireNonNull(endDate, "结束时间[endDate]不能为null");

		Instant start = startDate.toInstant();
		Instant end = endDate.toInstant();
		return ChronoUnit.MINUTES.between(start, end);
	}

	/**
	 * 比较两个Date的间隔秒数（绝对时间差，每1000毫秒为1秒）
	 * <p>不依赖时区，基于时间戳计算（如1秒999毫秒 → 1秒）</p>
	 *
	 * @param startDate 开始时间（非空）
	 * @param endDate   结束时间（非空）
	 * @return 间隔秒数（endDate - startDate的完整秒，可为负数）
	 * @throws NullPointerException 若startDate或endDate为null
	 * @example ① 10:00:00.000 与 10:00:30.500 → 30秒
	 * @example ② 10:00:30.999 与 10:00:31.000 → 0秒（不足1秒）
	 */
	public static long betweenSeconds(Date startDate, Date endDate) {
		Objects.requireNonNull(startDate, "开始时间[startDate]不能为null");
		Objects.requireNonNull(endDate, "结束时间[endDate]不能为null");

		Instant start = startDate.toInstant();
		Instant end = endDate.toInstant();
		return ChronoUnit.SECONDS.between(start, end);
	}

	/**
	 * 比较两个Date的间隔毫秒数（绝对时间差）
	 * <p>不依赖时区，直接计算时间戳差值（1毫秒=1/1000秒）</p>
	 *
	 * @param startDate 开始时间（非空）
	 * @param endDate   结束时间（非空）
	 * @return 间隔毫秒数（endDate时间戳 - startDate时间戳，可为负数）
	 * @throws NullPointerException 若startDate或endDate为null
	 * @example ① 时间戳1620000000000 与 1620000001500 → 1500毫秒
	 * @example ② 后一时间早于前一时间 → 返回负数
	 */
	public static long betweenMillis(Date startDate, Date endDate) {
		Objects.requireNonNull(startDate, "开始时间[startDate]不能为null");
		Objects.requireNonNull(endDate, "结束时间[endDate]不能为null");

		long startMillis = startDate.toInstant().toEpochMilli();
		long endMillis = endDate.toInstant().toEpochMilli();
		return endMillis - startMillis;
	}


	// ========================== 六、特有功能 ==========================

	/**
	 * 秒数转换为日时分秒字符串
	 *
	 * @param second 秒数
	 * @return 格式化后的时间字符串（如 "1天2小时30分15秒"）
	 */
	public static String secondToTime(Long second) {
		if (second == null || second == 0L) {
			return StringPool.EMPTY;
		}
		long days = second / 86400;
		second = second % 86400;
		long hours = second / 3600;
		second = second % 3600;
		long minutes = second / 60;
		second = second % 60;

		return days > 0 ?
				StringUtil.format("{}天{}小时{}分{}秒", days, hours, minutes, second) :
				StringUtil.format("{}小时{}分{}秒", hours, minutes, second);
	}

	/**
	 * 获取当前日期字符串（yyyy-MM-dd）
	 *
	 * @return 今天日期字符串
	 */
	public static String today() {
		return DateTimeUtil.formatDate(DateTimeUtil.now());
	}

	/**
	 * 获取当前时间字符串（yyyy-MM-dd HH:mm:ss）
	 *
	 * @return 当前时间字符串
	 */
	public static String time() {
		return DateTimeUtil.formatDateTime(DateTimeUtil.now());
	}

	/**
	 * 获取当前小时数（24小时制）
	 *
	 * @return 当前小时（0-23）
	 */
	public static Integer hour() {
		return DateTimeUtil.now().getHour();
	}


	// ========================== 七、常用时间点获取 ==========================

	/**
	 * 获取今年起始时间（Date 类型）
	 *
	 * @return 1月1日 00:00:00.000
	 */
	public static Date getYearStart() {
		return DateTimeUtil.toDate(DateTimeUtil.getYearStart());
	}

	/**
	 * 获取今年结束时间（Date 类型）
	 *
	 * @return 12月31日 23:59:59.999
	 */
	public static Date getYearEnd() {
		return DateTimeUtil.toDate(DateTimeUtil.getYearEnd());
	}

	/**
	 * 获取当前季度起始时间（Date 类型）
	 *
	 * @return 季度第一天 00:00:00.000
	 */
	public static Date getQuarterStart() {
		return DateTimeUtil.toDate(DateTimeUtil.getQuarterStart());
	}

	/**
	 * 获取当前季度结束时间（Date 类型）
	 *
	 * @return 季度最后一天 23:59:59.999
	 */
	public static Date getQuarterEnd() {
		return DateTimeUtil.toDate(DateTimeUtil.getQuarterEnd());
	}

	/**
	 * 获取当月起始时间（Date 类型）
	 *
	 * @return 当月1日 00:00:00.000
	 */
	public static Date getMonthStart() {
		return DateTimeUtil.toDate(DateTimeUtil.getMonthStart());
	}

	/**
	 * 获取当月结束时间（Date 类型）
	 *
	 * @return 当月最后一天 23:59:59.999
	 */
	public static Date getMonthEnd() {
		return DateTimeUtil.toDate(DateTimeUtil.getMonthEnd());
	}

	/**
	 * 获取当周起始时间（Date 类型）
	 *
	 * @return 周一 00:00:00.000
	 */
	public static Date getWeekStart() {
		return DateTimeUtil.toDate(DateTimeUtil.getWeekStart());
	}

	/**
	 * 获取当周结束时间（Date 类型）
	 *
	 * @return 周日 23:59:59.999
	 */
	public static Date getWeekEnd() {
		return DateTimeUtil.toDate(DateTimeUtil.getWeekEnd());
	}

	/**
	 * 获取当天起始时间（Date 类型）
	 *
	 * @return 00:00:00.000
	 */
	public static Date getDayStart() {
		return DateTimeUtil.toDate(DateTimeUtil.getDayStart());
	}

	/**
	 * 获取当天中午时间（Date 类型）
	 *
	 * @return 12:00:00
	 */
	public static Date getDayNoon() {
		return DateTimeUtil.toDate(DateTimeUtil.getDayNoon());
	}

	/**
	 * 获取当天结束时间（Date 类型）
	 *
	 * @return 23:59:59.999
	 */
	public static Date getDayEnd() {
		return DateTimeUtil.toDate(DateTimeUtil.getDayEnd());
	}

	/**
	 * 获取指定 Date 的起始时间（00:00:00.000）
	 *
	 * @param date 目标时间
	 * @return 指定日期的起始时间
	 */
	public static Date getDayStart(Date date) {
		Assert.notNull(date, "The date must not be null");
		LocalDateTime dt = DateTimeUtil.toDateTime(date);
		LocalDateTime dayStart = DateTimeUtil.getDayStart(dt.toLocalDate());
		return DateTimeUtil.toDate(dayStart);
	}

	/**
	 * 获取指定 Date 的结束时间（23:59:59.999）
	 *
	 * @param date 目标时间
	 * @return 指定日期的结束时间
	 */
	public static Date getDayEnd(Date date) {
		Assert.notNull(date, "The date must not be null");
		LocalDateTime dt = DateTimeUtil.toDateTime(date);
		LocalDateTime dayEnd = DateTimeUtil.getDayEnd(dt.toLocalDate());
		return DateTimeUtil.toDate(dayEnd);
	}


	// ========================== 私有工具方法 ==========================

	/**
	 * Date 转换为 LocalDateTime 并执行操作，再转回 Date
	 *
	 * @param date    目标 Date
	 * @param handler 时间操作逻辑
	 * @return 操作后的 Date
	 */
	private static Date convertAndOperate(Date date, Function<LocalDateTime, LocalDateTime> handler) {
		LocalDateTime dt = DateTimeUtil.toDateTime(date);
		dt = handler.apply(dt);
		return DateTimeUtil.toDate(dt);
	}

	/**
	 * Date转换为系统时区的LocalDate（用于日历维度计算）
	 */
	private static LocalDate toLocalDate(Date date) {
		// 使用系统默认时区转换（年月计算依赖时区，如跨时区的"同一天"可能属于不同日期）
		return date.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDate();
	}
}
