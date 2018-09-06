package com.juyoufuli.common.lang;


import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 * @version 2017-1-4
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

	// 以T分隔日期和时间，并带时区信息，符合ISO8601规范
	public static final String PATTERN_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";
	public static final String PATTERN_ISO_ON_SECOND = "yyyy-MM-dd'T'HH:mm:ssZZ";
	public static final String PATTERN_ISO_ON_DATE = "yyyy-MM-dd";

	// 以空格分隔日期和时间，不带时区信息
	public static final String PATTERN_DEFAULT = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String PATTERN_DEFAULT_ON_SECOND = "yyyy-MM-dd HH:mm:ss";

	// 使用工厂方法FastDateFormat.getInstance(), 从缓存中获取实例

	// 以T分隔日期和时间，并带时区信息，符合ISO8601规范
	public static final FastDateFormat ISO_FORMAT = FastDateFormat.getInstance(PATTERN_ISO);
	public static final FastDateFormat ISO_ON_SECOND_FORMAT = FastDateFormat.getInstance(PATTERN_ISO_ON_SECOND);
	public static final FastDateFormat ISO_ON_DATE_FORMAT = FastDateFormat.getInstance(PATTERN_ISO_ON_DATE);

	// 以空格分隔日期和时间，不带时区信息
	public static final FastDateFormat DEFAULT_FORMAT = FastDateFormat.getInstance(PATTERN_DEFAULT);
	public static final FastDateFormat DEFAULT_ON_SECOND_FORMAT = FastDateFormat.getInstance(PATTERN_DEFAULT_ON_SECOND);

	public static final long MILLIS_PER_SECOND = 1000; // Number of milliseconds in a standard second.
	public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND; // Number of milliseconds in a standard minute.
	public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE; // Number of milliseconds in a standard hour.
	public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR; // Number of milliseconds in a standard day.

	private static final int[] MONTH_LENGTH = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

	/**
	 * 分析日期字符串, 仅用于pattern不固定的情况.
	 *
	 * 否则直接使用DateFormats中封装好的FastDateFormat.
	 *
	 * FastDateFormat.getInstance()已经做了缓存，不会每次创建对象，但直接使用对象仍然能减少在缓存中的查找.
	 */
	public static Date parseDate(String pattern,  String dateString) throws ParseException {
		return FastDateFormat.getInstance(pattern).parse(dateString);
	}

	/**
	 * 格式化日期, 仅用于pattern不固定的情况.
	 *
	 * 否则直接使用本类中封装好的FastDateFormat.
	 *
	 * FastDateFormat.getInstance()已经做了缓存，不会每次创建对象，但直接使用对象仍然能减少在缓存中的查找.
	 */
	public static String formatDate(String pattern, Date date) {
		return FastDateFormat.getInstance(pattern).format(date);
	}

	/**
	 * 格式化日期, 仅用于不固定pattern不固定的情况.
	 *
	 * 否否则直接使用本类中封装好的FastDateFormat.
	 *
	 * FastDateFormat.getInstance()已经做了缓存，不会每次创建对象，但直接使用对象仍然能减少在缓存中的查找.
	 */
	public static String formatDate(String pattern, long date) {
		return FastDateFormat.getInstance(pattern).format(date);
	}

	/////// 格式化间隔时间/////////
	/**
	 * 按HH:mm:ss.SSS格式，格式化时间间隔.
	 *
	 * endDate必须大于startDate，间隔可大于1天，
	 *
	 * @see DurationFormatUtils
	 */
	public static String formatDuration(Date startDate, Date endDate) {
		return DurationFormatUtils.formatDurationHMS(endDate.getTime() - startDate.getTime());
	}

	/**
	 * 按HH:mm:ss.SSS格式，格式化时间间隔
	 *
	 * 单位为毫秒，必须大于0，可大于1天
	 *
	 * @see DurationFormatUtils
	 */
	public static String formatDuration(long durationMillis) {
		return DurationFormatUtils.formatDurationHMS(durationMillis);
	}

	/**
	 * 按HH:mm:ss格式，格式化时间间隔
	 *
	 * endDate必须大于startDate，间隔可大于1天
	 *
	 * @see DurationFormatUtils
	 */
	public static String formatDurationOnSecond(Date startDate,  Date endDate) {
		return DurationFormatUtils.formatDuration(endDate.getTime() - startDate.getTime(), "HH:mm:ss");
	}

	/**
	 * 按HH:mm:ss格式，格式化时间间隔
	 *
	 * 单位为毫秒，必须大于0，可大于1天
	 *
	 * @see DurationFormatUtils
	 */
	public static String formatDurationOnSecond(long durationMillis) {
		return DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss");
	}


	//////// 打印用于页面显示的用户友好，与当前时间比的时间差
	/**
	 * 打印用户友好的，与当前时间相比的时间差，如刚刚，5分钟前，今天XXX，昨天XXX
	 *
	 * copy from AndroidUtilCode
	 */
	public static String formatFriendlyTimeSpanByNow(Date date) {
		return formatFriendlyTimeSpanByNow(date.getTime());
	}

	/**
	 * 打印用户友好的，与当前时间相比的时间差，如刚刚，5分钟前，今天XXX，昨天XXX
	 *
	 * copy from AndroidUtilCode
	 */
	public static String formatFriendlyTimeSpanByNow(long timeStampMillis) {
		long now = System.currentTimeMillis();
		long span = now - timeStampMillis;
		if (span < 0) {
			// 'c' 日期和时间，被格式化为 "%ta %tb %td %tT %tZ %tY"，例如 "Sun Jul 20 16:17:00 EDT 1969"。
			return String.format("%tc", timeStampMillis);
		}
		if (span < DateUtils.MILLIS_PER_SECOND) {
			return "刚刚";
		} else if (span < DateUtils.MILLIS_PER_MINUTE) {
			return String.format("%d秒前", span / DateUtils.MILLIS_PER_SECOND);
		} else if (span < DateUtils.MILLIS_PER_HOUR) {
			return String.format("%d分钟前", span / DateUtils.MILLIS_PER_MINUTE);
		}
		// 获取当天00:00
		long wee = DateUtils.beginOfDate(new Date(now)).getTime();
		if (timeStampMillis >= wee) {
			// 'R' 24 小时制的时间，被格式化为 "%tH:%tM"
			return String.format("今天%tR", timeStampMillis);
		} else if (timeStampMillis >= wee - DateUtils.MILLIS_PER_DAY) {
			return String.format("昨天%tR", timeStampMillis);
		} else {
			// 'F' ISO 8601 格式的完整日期，被格式化为 "%tY-%tm-%td"。
			return String.format("%tF", timeStampMillis);
		}
	}


	//////// 日期比较 ///////////
	/**
	 * 是否同一天.
	 *
	 * @see DateUtils#isSameDay(Date, Date)
	 */
	public static boolean isSameDay(final Date date1,  final Date date2) {
		return org.apache.commons.lang3.time.DateUtils.isSameDay(date1, date2);
	}

	/**
	 * 是否同一时刻.
	 */
	public static boolean isSameTime(final Date date1,  final Date date2) {
		// date.getMillisOf() 比date.getTime()快
		return date1.compareTo(date2) == 0;
	}

	/**
	 * 判断日期是否在范围内，包含相等的日期
	 */
	public static boolean isBetween(final Date date,  final Date start, final Date end) {
		if (date == null || start == null || end == null || start.after(end)) {
			throw new IllegalArgumentException("some date parameters is null or dateBein after dateEnd");
		}
		return !date.before(start) && !date.after(end);
	}

	//////////// 往前往后滚动时间//////////////

	/**
	 * 加一月
	 */
	public static Date addMonths(final Date date, int amount) {
		return org.apache.commons.lang3.time.DateUtils.addMonths(date, amount);
	}

	/**
	 * 减一月
	 */
	public static Date subMonths(final Date date, int amount) {
		return org.apache.commons.lang3.time.DateUtils.addMonths(date, -amount);
	}

	/**
	 * 加一周
	 */
	public static Date addWeeks(final Date date, int amount) {
		return org.apache.commons.lang3.time.DateUtils.addWeeks(date, amount);
	}

	/**
	 * 减一周
	 */
	public static Date subWeeks(final Date date, int amount) {
		return org.apache.commons.lang3.time.DateUtils.addWeeks(date, -amount);
	}

	/**
	 * 加一天
	 */
	public static Date addDays(final Date date, final int amount) {
		return org.apache.commons.lang3.time.DateUtils.addDays(date, amount);
	}

	/**
	 * 减一天
	 */
	public static Date subDays(final Date date, int amount) {
		return org.apache.commons.lang3.time.DateUtils.addDays(date, -amount);
	}

	/**
	 * 加一小时
	 */
	public static Date addHours(final Date date, int amount) {
		return org.apache.commons.lang3.time.DateUtils.addHours(date, amount);
	}

	/**
	 * 减一小时
	 */
	public static Date subHours(final Date date, int amount) {
		return org.apache.commons.lang3.time.DateUtils.addHours(date, -amount);
	}

	/**
	 * 加一分钟
	 */
	public static Date addMinutes(final Date date, int amount) {
		return org.apache.commons.lang3.time.DateUtils.addMinutes(date, amount);
	}

	/**
	 * 减一分钟
	 */
	public static Date subMinutes(final Date date, int amount) {
		return org.apache.commons.lang3.time.DateUtils.addMinutes(date, -amount);
	}

	/**
	 * 终于到了，续一秒.
	 */
	public static Date addSeconds(final Date date, int amount) {
		return org.apache.commons.lang3.time.DateUtils.addSeconds(date, amount);
	}

	/**
	 * 减一秒.
	 */
	public static Date subSeconds(final Date date, int amount) {
		return org.apache.commons.lang3.time.DateUtils.addSeconds(date, -amount);
	}

	//////////// 直接设置时间//////////////

	/**
	 * 设置年份, 公元纪年.
	 */
	public static Date setYears(final Date date, int amount) {
		return org.apache.commons.lang3.time.DateUtils.setYears(date, amount);
	}

	/**
	 * 设置月份, 0-11.
	 */
	public static Date setMonths(final Date date, int amount) {
		return org.apache.commons.lang3.time.DateUtils.setMonths(date, amount);
	}

	/**
	 * 设置日期, 1-31.
	 */
	public static Date setDays(final Date date, int amount) {
		return org.apache.commons.lang3.time.DateUtils.setDays(date, amount);
	}

	/**
	 * 设置小时, 0-23.
	 */
	public static Date setHours(final Date date, int amount) {
		return org.apache.commons.lang3.time.DateUtils.setHours(date, amount);
	}

	/**
	 * 设置分钟, 0-59.
	 */
	public static Date setMinutes(final Date date, int amount) {
		return org.apache.commons.lang3.time.DateUtils.setMinutes(date, amount);
	}

	/**
	 * 设置秒, 0-59.
	 */
	public static Date setSeconds(final Date date, int amount) {
		return org.apache.commons.lang3.time.DateUtils.setSeconds(date, amount);
	}

	/**
	 * 设置毫秒.
	 */
	public static Date setMilliseconds(final Date date, int amount) {
		return org.apache.commons.lang3.time.DateUtils.setMilliseconds(date, amount);
	}

	///// 获取日期的位置//////
	/**
	 * 获得日期是一周的第几天. 已改为中国习惯，1 是Monday，而不是Sundays.
	 */
	public static int getDayOfWeek(final Date date) {
		int result = getWithMondayFirst(date, Calendar.DAY_OF_WEEK);
		return result == 1 ? 7 : result - 1;
	}

	/**
	 * 获得日期是一年的第几天，返回值从1开始
	 */
	public static int getDayOfYear(final Date date) {
		return get(date, Calendar.DAY_OF_YEAR);
	}

	/**
	 * 获得日期是一月的第几周，返回值从1开始.
	 *
	 * 开始的一周，只要有一天在那个月里都算. 已改为中国习惯，1 是Monday，而不是Sunday
	 */
	public static int getWeekOfMonth(final Date date) {
		return getWithMondayFirst(date, Calendar.WEEK_OF_MONTH);
	}

	/**
	 * 获得日期是一年的第几周，返回值从1开始.
	 *
	 * 开始的一周，只要有一天在那一年里都算.已改为中国习惯，1 是Monday，而不是Sunday
	 */
	public static int getWeekOfYear(final Date date) {
		return getWithMondayFirst(date, Calendar.WEEK_OF_YEAR);
	}

	private static int get(final Date date, int field) {
		Validate.notNull(date, "The date must not be null");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		return cal.get(field);
	}

	private static int getWithMondayFirst(final Date date, int field) {
		Validate.notNull(date, "The date must not be null");
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.setTime(date);
		return cal.get(field);
	}

	///// 获得往前往后的日期//////

	/**
	 * 2016-11-10 07:33:23, 则返回2016-1-1 00:00:00
	 */
	public static Date beginOfYear(final Date date) {
		return org.apache.commons.lang3.time.DateUtils.truncate(date, Calendar.YEAR);
	}

	/**
	 * 2016-11-10 07:33:23, 则返回2016-12-31 23:59:59.999
	 */
	public static Date endOfYear(final Date date) {
		return new Date(nextYear(date).getTime() - 1);
	}

	/**
	 * 2016-11-10 07:33:23, 则返回2017-1-1 00:00:00
	 */
	public static Date nextYear(final Date date) {
		return org.apache.commons.lang3.time.DateUtils.ceiling(date, Calendar.YEAR);
	}

	/**
	 * 2016-11-10 07:33:23, 则返回2016-11-1 00:00:00
	 */
	public static Date beginOfMonth(final Date date) {
		return org.apache.commons.lang3.time.DateUtils.truncate(date, Calendar.MONTH);
	}

	/**
	 * 2016-11-10 07:33:23, 则返回2016-11-30 23:59:59.999
	 */
	public static Date endOfMonth(final Date date) {
		return new Date(nextMonth(date).getTime() - 1);
	}

	/**
	 * 2016-11-10 07:33:23, 则返回2016-12-1 00:00:00
	 */
	public static Date nextMonth(final Date date) {
		return org.apache.commons.lang3.time.DateUtils.ceiling(date, Calendar.MONTH);
	}

	/**
	 * 2017-1-20 07:33:23, 则返回2017-1-16 00:00:00
	 */
	public static Date beginOfWeek(final Date date) {
		return org.apache.commons.lang3.time.DateUtils.truncate(DateUtils.subDays(date, DateUtils.getDayOfWeek(date) - 1), Calendar.DATE);
	}

	/**
	 * 2017-1-20 07:33:23, 则返回2017-1-22 23:59:59.999
	 */
	public static Date endOfWeek(final Date date) {
		return new Date(nextWeek(date).getTime() - 1);
	}

	/**
	 * 2017-1-23 07:33:23, 则返回2017-1-22 00:00:00
	 */
	public static Date nextWeek(final Date date) {
		return org.apache.commons.lang3.time.DateUtils.truncate(DateUtils.addDays(date, 8 - DateUtils.getDayOfWeek(date)), Calendar.DATE);
	}

	/**
	 * 2016-11-10 07:33:23, 则返回2016-11-10 00:00:00
	 */
	public static Date beginOfDate(final Date date) {
		return org.apache.commons.lang3.time.DateUtils.truncate(date, Calendar.DATE);
	}

	/**
	 * 2017-1-23 07:33:23, 则返回2017-1-23 23:59:59.999
	 */
	public static Date endOfDate(final Date date) {
		return new Date(nextDate(date).getTime() - 1);
	}

	/**
	 * 2016-11-10 07:33:23, 则返回2016-11-11 00:00:00
	 */
	public static Date nextDate(final Date date) {
		return org.apache.commons.lang3.time.DateUtils.ceiling(date, Calendar.DATE);
	}

	/**
	 * 2016-12-10 07:33:23, 则返回2016-12-10 07:00:00
	 */
	public static Date beginOfHour(final Date date) {
		return org.apache.commons.lang3.time.DateUtils.truncate(date, Calendar.HOUR_OF_DAY);
	}

	/**
	 * 2017-1-23 07:33:23, 则返回2017-1-23 07:59:59.999
	 */
	public static Date endOfHour(final Date date) {
		return new Date(nextHour(date).getTime() - 1);
	}

	/**
	 * 2016-12-10 07:33:23, 则返回2016-12-10 08:00:00
	 */
	public static Date nextHour(final Date date) {
		return org.apache.commons.lang3.time.DateUtils.ceiling(date, Calendar.HOUR_OF_DAY);
	}

	/**
	 * 2016-12-10 07:33:23, 则返回2016-12-10 07:33:00
	 */
	public static Date beginOfMinute(final Date date) {
		return org.apache.commons.lang3.time.DateUtils.truncate(date, Calendar.MINUTE);
	}

	/**
	 * 2017-1-23 07:33:23, 则返回2017-1-23 07:33:59.999
	 */
	public static Date endOfMinute(final Date date) {
		return new Date(nextMinute(date).getTime() - 1);
	}

	/**
	 * 2016-12-10 07:33:23, 则返回2016-12-10 07:34:00
	 */
	public static Date nextMinute(final Date date) {
		return org.apache.commons.lang3.time.DateUtils.ceiling(date, Calendar.MINUTE);
	}

	////// 闰年及每月天数///////
	/**
	 * 是否闰年.
	 */
	public static boolean isLeapYear(final Date date) {
		return isLeapYear(get(date, Calendar.YEAR));
	}

	/**
	 * 是否闰年，copy from Jodd Core的TimeUtil
	 *
	 * 参数是公元计数, 如2016
	 */
	public static boolean isLeapYear(int y) {
		boolean result = false;

		if (((y % 4) == 0) && // must be divisible by 4...
				((y < 1582) || // and either before reform year...
						((y % 100) != 0) || // or not a century...
						((y % 400) == 0))) { // or a multiple of 400...
			result = true; // for leap year.
		}
		return result;
	}

	/**
	 * 获取某个月有多少天, 考虑闰年等因数, 移植Jodd Core的TimeUtil
	 */
	public static int getMonthLength(final Date date) {
		int year = get(date, Calendar.YEAR);
		int month = get(date, Calendar.MONTH);
		return getMonthLength(year, month);
	}

	/**
	 * 获取某个月有多少天, 考虑闰年等因数, 移植Jodd Core的TimeUtil
	 */
	public static int getMonthLength(int year, int month) {

		if ((month < 1) || (month > 12)) {
			throw new IllegalArgumentException("Invalid month: " + month);
		}
		if (month == 2) {
			return isLeapYear(year) ? 29 : 28;
		}

		return MONTH_LENGTH[month];
	}

}
