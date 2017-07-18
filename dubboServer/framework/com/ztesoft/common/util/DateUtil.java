package com.ztesoft.common.util;

import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.log4j.Logger;

/**
 * 日期工具类
 * 
 * @author Administrator
 *
 */
public class DateUtil {

	private static Logger logger = Logger.getLogger(DateUtil.class);

	// 系统默认日期格式
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	// 系统默认日期时间格式
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	// 8位日期格式
	public static final String DATE_FORMAT_8 = "yyyyMMdd";
	// 6位日期格式
	public static final String DATE_FORMAT_6 = "yyyyMM";
	// 14位日期时间格式
	public static final String DATE_TIME_FORMAT_14 = "yyyyMMddHHmmss";
	public static final String DATE_TIME_FORMAT_22 = "yyyy-MM-dd HH:mm:ss.SSS";

	public final static String YEAR = "year";

	public final static String MONTH = "month";

	public final static String DAY = "day";

	public final static String HOUR = "hour";

	public final static String MINUTE = "minute";

	public final static String SECOND = "second";

	public final static String WEEK = "week";

	/**
	 * <p>
	 * Description: 时间以及时间格式相关的处理功能
	 * </p>
	 * <p>
	 * 
	 * private static Logger logger = Logger.getLogger(DateUtil.class);
	 * 
	 * /** 得到应用服务器当前日期，以默认格式显示。
	 * 
	 * @return
	 */
	public static String getFormatedDate() {
		Date date = getCurrentDate();
		SimpleDateFormat dateFormator = new SimpleDateFormat(
				DateUtil.DATE_FORMAT);
		return dateFormator.format(date);

	}

	/**
	 * 得到应用服务器当前日期时间，以默认格式显示。
	 * 
	 * @return
	 */
	public static String getFormatedDateTime() {

		Date date = getCurrentDate();
		SimpleDateFormat dateFormator = new SimpleDateFormat(
				DateUtil.DATE_TIME_FORMAT);
		return dateFormator.format(date);

	}

	/**
	 * 得到应用服务器的当前时间
	 * 
	 * @return
	 */
	public static Date getCurrentDate() {
		return new Date(System.currentTimeMillis());
	}

	/**
	 * 得到应用服务器的当前时间，毫秒。
	 * 
	 * @return
	 */
	public static long getCurrentTimeMillis() {
		return System.currentTimeMillis();
	}

	/**
	 * 得到应用服务器当前日期 按照指定的格式返回。
	 * 
	 * @param pattern
	 *            格式类型，通过系统常量中定义，如：CapConstants.DATE_FORMAT_8
	 * @return
	 */
	public static String formatDate(String pattern) {

		Date date = new Date();
		SimpleDateFormat dateFormator = new SimpleDateFormat(pattern);
		String str = dateFormator.format(date);

		return str;
	}

	/**
	 * 转换输入日期 按照指定的格式返回。
	 * 
	 * @param date
	 * @param pattern
	 *            格式类型，通过系统常量中定义，如：CapConstants.DATE_FORMAT_8
	 * @return
	 */
	public static String formatDate(Date date, String pattern) {

		if (date == null) {
			return "";
		}

		SimpleDateFormat dateFormator = new SimpleDateFormat(pattern);
		String str = dateFormator.format(date);

		return str;
	}

	/**
	 * 转换输入日期 按照指定的格式返回。
	 * 
	 * @param date
	 * @param pattern
	 *            格式类型，通过系统常量中定义，如：CapConstants.DATE_FORMAT_8
	 * @param loc
	 *            locale
	 * @return
	 */
	public static String formatDate(Date date, String pattern, Locale loc) {
		if (pattern == null || date == null) {
			return "";
		}
		String newDate = "";
		if (loc == null) {
			loc = Locale.getDefault();
		}
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern, loc);
			newDate = sdf.format(date);
		}
		return newDate;
	}

	/**
	 * 将字符时间从一个格式转换成另一个格式。时间的格式，最好通过系统常量定义。 如： String dateStr = "2006-10-9
	 * 12:09:08"; DateFormatUtils.convertDateFormat(dateStr,
	 * CapConstants.DATE_TIME_FORMAT, CapConstants.DATE_FORMAT_8);
	 * 
	 * @param sdate
	 * @param patternFrom
	 *            格式类型，通过系统常量中定义，如：CapConstants.DATE_FORMAT_8
	 * @param patternTo
	 *            格式类型，通过系统常量中定义，如：CapConstants.DATE_FORMAT_8
	 * @return
	 */
	public static String convertDateFormat(String dateStr, String patternFrom,
			String patternTo) {

		if (dateStr == null || patternFrom == null || patternTo == null) {
			return "";
		}

		String newDate = "";

		try {

			Date dateFrom = parseStrToDate(dateStr, patternFrom);
			newDate = formatDate(dateFrom, patternTo);

		} catch (Exception e) {
		}

		return newDate;
	}

	/**
	 * 将时间串按照默认格式CapConstants.DATE_FORMAT，格式化成Date。
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Date parseStrToDate(String dateStr) {

		if (null == dateStr || "".equals(dateStr)) {
			return null;
		}

		SimpleDateFormat dateFormator = new SimpleDateFormat(
				DateUtil.DATE_FORMAT);

		java.util.Date tDate = dateFormator
				.parse(dateStr, new ParsePosition(0));

		return tDate;
	}

	public static String parseDateStrToDateTimeStr(String dateStr) {

		if (null == dateStr || "".equals(dateStr)) {
			return null;
		}

		SimpleDateFormat dateFormator = new SimpleDateFormat(
				DateUtil.DATE_FORMAT);

		java.util.Date tDate = dateFormator
				.parse(dateStr, new ParsePosition(0));
		return formatDate(tDate, DateUtil.DATE_TIME_FORMAT);

	}

	/**
	 * 将时间串按照默认格式CapConstants.DATE_FORMAT，格式化成Date。
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Calendar parseStrToCalendar(String dateStr) {

		if (null == dateStr || "".equals(dateStr)) {
			return null;
		}

		SimpleDateFormat dateFormator = new SimpleDateFormat(
				DateUtil.DATE_FORMAT);

		java.util.Date tDate = dateFormator
				.parse(dateStr, new ParsePosition(0));

		Locale loc = Locale.getDefault();
		Calendar cal = new GregorianCalendar(loc);
		cal.setTime(tDate);

		return cal;
	}

	public static Calendar parseStrToCalendar(String dateStr, String pattern) {

		if (null == dateStr || "".equals(dateStr)) {
			return null;
		}

		SimpleDateFormat dateFormator = new SimpleDateFormat(pattern);

		java.util.Date tDate = dateFormator
				.parse(dateStr, new ParsePosition(0));

		Locale loc = Locale.getDefault();
		Calendar cal = new GregorianCalendar(loc);
		cal.setTime(tDate);

		return cal;
	}

	/**
	 * 将时间串按照默认格式CapConstants.DATE_TIME_FORMAT，格式化成Date。
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Date parseStrToDateTime(String dateStr) {

		if (null == dateStr || "".equals(dateStr)) {
			return null;
		}

		SimpleDateFormat dateFormator = new SimpleDateFormat(
				DateUtil.DATE_TIME_FORMAT);

		java.util.Date tDate = dateFormator
				.parse(dateStr, new ParsePosition(0));

		return tDate;
	}

	/**
	 * 将时间串按照指定格式，格式化成Date。
	 * 
	 * @param date
	 * @param pattern
	 *            格式类型，通过系统常量中定义，如：CapConstants.DATE_FORMAT_8
	 * @return
	 */

	public static Date parseStrToDate(String dateStr, String pattern) {
		if (null == dateStr || "".equals(dateStr)) {
			return null;
		}

		SimpleDateFormat dateFormator = new SimpleDateFormat(pattern);

		java.util.Date tDate = dateFormator
				.parse(dateStr, new ParsePosition(0));

		return tDate;
	}

	/**
	 * 按时间格式相加：
	 * 输入要相加的时间基点（字符串型或时间类型），相加的时长（整型），格式（"year"年、"month"月、"day"日、”hour“
	 * 时、”minute“分、”second“秒、"week"周） 输出相加后的时间（字符串型或时间类型）
	 * 
	 * @param dateStr
	 * @param pattern
	 * @param step
	 * @param type
	 *            "year"年、"month"月、"day"日、”hour“时、”minute“分、”second“秒、"week"周
	 *            通过常量DateFormatUtils.YEAR等来设置.
	 * @return
	 */
	public static String addDate(String dateStr, String pattern, int step,
			String type) {
		if (dateStr == null) {
			return dateStr;
		} else {
			Date date1 = DateUtil.parseStrToDate(dateStr, pattern);

			Locale loc = Locale.getDefault();
			Calendar cal = new GregorianCalendar(loc);
			cal.setTime(date1);

			if (DateUtil.WEEK.equals(type)) {

				cal.add(Calendar.WEEK_OF_MONTH, step);

			} else if (DateUtil.YEAR.equals(type)) {

				cal.add(Calendar.YEAR, step);

			} else if (DateUtil.MONTH.equals(type)) {

				cal.add(Calendar.MONTH, step);

			} else if (DateUtil.DAY.equals(type)) {

				cal.add(Calendar.DAY_OF_MONTH, step);

			} else if (DateUtil.HOUR.equals(type)) {

				cal.add(Calendar.HOUR, step);

			} else if (DateUtil.MINUTE.equals(type)) {

				cal.add(Calendar.MINUTE, step);

			} else if (DateUtil.SECOND.equals(type)) {

				cal.add(Calendar.SECOND, step);

			}

			return DateUtil.formatDate(cal.getTime(), pattern);
		}
	}

	/**
	 * 按时间格式相减：
	 * 输入要相加的时间基点（字符串型或时间类型），相加的时长（整型），格式（"year"年、"month"月、"day"日、”hour“
	 * 时、”minute“分、”second“秒、"week"周） 输出相加后的时间（字符串型或时间类型）
	 * 
	 * @param dateStr
	 * @param pattern
	 * @param step
	 * @param type
	 *            "year"年、"month"月、"day"日、”hour“时、”minute“分、”second“秒、"week"周
	 * @return
	 */
	public static String minusDate(String dateStr, String pattern, int step,
			String type) {

		return addDate(dateStr, pattern, (0 - step), type);

	}

	/**
	 * 日期增加天数
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date addDay(Date date, int days) {
		if (date == null) {
			return date;
		} else {
			Locale loc = Locale.getDefault();
			Calendar cal = new GregorianCalendar(loc);
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_MONTH, days);
			return cal.getTime();
		}
	}

	public static Timestamp addDay(Timestamp time, int days) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time.getTime());
		c.add(6, days);
		return new Timestamp(c.getTimeInMillis());
	}

	public static Date addMonth(Date date, int month) {
		if (date == null) {
			return date;
		} else {
			Locale loc = Locale.getDefault();
			Calendar cal = new GregorianCalendar(loc);
			cal.setTime(date);
			cal.add(Calendar.MONTH, month);
			return cal.getTime();
		}
	}

	public static int getDays(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return 0;
		} else {
			return (int) ((date2.getTime() - date1.getTime()) / 0x5265c00L);
		}
	}

	/**
	 * 日期比较大小
	 * 
	 * @param dateStr1
	 * @param dateStr2
	 * @param pattern
	 * @return
	 */
	public static int compareDate(String dateStr1, String dateStr2,
			String pattern) {

		Date date1 = DateUtil.parseStrToDate(dateStr1, pattern);
		Date date2 = DateUtil.parseStrToDate(dateStr2, pattern);

		return date1.compareTo(date2);

	}

	/**
	 * @param dateStr
	 * @param pattern
	 * @return
	 */
	public static String getFirstDayInMonth(String dateStr, String pattern) {
		Calendar cal = DateUtil.parseStrToCalendar(dateStr);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);

		cal.add(Calendar.DAY_OF_MONTH, (1 - day));

		return DateUtil.formatDate(cal.getTime(), pattern);
	}

	/**
	 * @param dateStr
	 * @param pattern
	 * @return
	 */
	public static String getLastDayInMonth(String dateStr, String pattern) {
		Calendar cal = DateUtil.parseStrToCalendar(dateStr);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);

		int maxDayInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		int step = maxDayInMonth - day;

		cal.add(Calendar.DAY_OF_MONTH, step);

		return DateUtil.formatDate(cal.getTime(), pattern);
	}

	/**
	 * @param dateStr
	 * @param pattern
	 * @return
	 */
	public static String getFirstDayInWeek(String dateStr, String pattern) {
		Calendar cal = DateUtil.parseStrToCalendar(dateStr);
		int day = cal.get(Calendar.DAY_OF_WEEK);

		cal.add(Calendar.DAY_OF_MONTH, (1 - day));

		return DateUtil.formatDate(cal.getTime(), pattern);
	}

	/**
	 * @param dateStr
	 * @param pattern
	 * @return
	 */
	public static String getLastDayInWeek(String dateStr, String pattern) {
		Calendar cal = DateUtil.parseStrToCalendar(dateStr);
		int day = cal.get(Calendar.DAY_OF_WEEK);

		cal.add(Calendar.DAY_OF_MONTH, (6 - day));

		return DateUtil.formatDate(cal.getTime(), pattern);
	}

	public static long calendarDayPlus(String dateStr1, String dateStr2) {

		if (dateStr1 == null || dateStr2 == null || dateStr1.equals("")
				|| dateStr2.equals("")) {
			return 0;
		}
		Date date1 = DateUtil.parseStrToDate(dateStr1);
		Date date2 = DateUtil.parseStrToDate(dateStr2);
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);
		long t1 = c1.getTimeInMillis();
		long t2 = c2.getTimeInMillis();
		long day = (t2 - t1) / (1000 * 60 * 60 * 24);
		return day;
	}

	/**
	 * @param dateStr
	 * @param pattern
	 * @return
	 */
	public static int calendarPlus(String dateStr1, String dateStr2) {

		if (dateStr1 == null || dateStr2 == null || dateStr1.equals("")
				|| dateStr2.equals("")) {
			return 0;
		}

		Calendar cal1 = DateUtil.parseStrToCalendar(dateStr1);

		Calendar cal2 = DateUtil.parseStrToCalendar(dateStr2);

		int dataStr1Year = cal1.get(Calendar.YEAR);
		int dataStr2Year = cal2.get(Calendar.YEAR);

		int dataStr1Month = cal1.get(Calendar.MONTH);
		int dataStr2Month = cal2.get(Calendar.MONTH);

		return ((dataStr2Year * 12 + dataStr2Month + 1) - (dataStr1Year * 12 + dataStr1Month));

	}

	public static void main(String[] argv) {

		String dateStr = "2004-2-18 12:13:34";
		String dateStr1 = "2006-7-18 12:13:34";
		String dateStr2 = "2006-7-18 12:13:34";
		// add
		Date date = DateUtil
				.addDay(DateUtil.parseStrToDate(dateStr,
						DateUtil.DATE_TIME_FORMAT), 20);

		logger.debug("after add "
				+ DateUtil.formatDate(date, DateUtil.DATE_TIME_FORMAT));
		// add
		System.out.println("add2 "
				+ DateUtil.addDate(dateStr, DateUtil.DATE_TIME_FORMAT, 2,
						DateUtil.HOUR));
		// sub
		System.out.println("minus "
				+ DateUtil.minusDate(dateStr, DateUtil.DATE_TIME_FORMAT, 2,
						DateUtil.WEEK));

		// get day
		// logger.debug(DateUtil.parseStrToCalendar(dateStr).get(
		// Calendar.DAY_OF_MONTH));
		// get month 要加1才是当前月数
		// logger.debug(DateUtil.parseStrToCalendar(dateStr).get(
		// Calendar.MONTH) + 1);
		// // get year
		// logger.debug(DateUtil.parseStrToCalendar(dateStr).get(
		// Calendar.YEAR));
		// // get hour
		// logger.debug(DateUtil.parseStrToCalendar(dateStr).get(
		// Calendar.HOUR_OF_DAY));
		// // get minute
		// logger.debug(DateUtil.parseStrToCalendar(dateStr).get(
		// Calendar.MINUTE));
		// // get second
		// logger.debug(DateUtil.parseStrToCalendar(dateStr).get(
		// Calendar.SECOND));
		//
		// // compareDate
		// logger.debug(DateUtil.compareDate(dateStr1,
		// dateStr2,
		// CapConstants.DATE_TIME_FORMAT));

		// first or last
		// logger.debug(DateUtil.getFirstDayInWeek(dateStr,
		// CapConstants.DATE_TIME_FORMAT));
		// logger.debug(DateUtil.getLastDayInWeek(dateStr,
		// CapConstants.DATE_TIME_FORMAT));
		// logger.debug(DateUtil.getFirstDayInMonth(dateStr,
		// CapConstants.DATE_TIME_FORMAT));
		// logger.debug(DateUtil.getLastDayInMonth(dateStr,
		// CapConstants.DATE_TIME_FORMAT));
		//
		// logger.debug(DateUtil.parseStrToCalendar(dateStr)
		// .getActualMaximum(Calendar.DAY_OF_MONTH));
		// System.out.println("UATime="+getUaDateTime());

	}

	/**
	 * 得到应用服务器当前日期，以8位日期显示。
	 * 
	 * @return
	 */
	public static String getDate() {

		Date date = getCurrentDate();
		SimpleDateFormat dateFormator = new SimpleDateFormat(
				DateUtil.DATE_FORMAT_8);
		return dateFormator.format(date);

	}

	/**
	 * 得到应用服务器当前日期，以8位日期显示。
	 * 
	 * @return
	 */
	public static String getDateTime() {

		Date date = getCurrentDate();
		SimpleDateFormat dateFormator = new SimpleDateFormat(
				DateUtil.DATE_TIME_FORMAT_14);
		return dateFormator.format(date);

	}

	/**
	 * 得到应用服务器当前日期，以DATE_TIME_FORMAT_14 = "yyyyMMddHHmmss"; 位日期显示。
	 * 
	 * @return
	 */
	public static String getVSOPDateTime14() {

		Date date = getCurrentDate();
		SimpleDateFormat dateFormator = new SimpleDateFormat(
				DateUtil.DATE_TIME_FORMAT_14);
		return dateFormator.format(date);

	}

	/**
	 * 得到应用服务器当前日期，以DATE_FORMAT_8 = "yyyyMMdd"; 位日期显示。
	 * 
	 * @return
	 */
	public static String getVSOPDate8() {

		Date date = getCurrentDate();
		SimpleDateFormat dateFormator = new SimpleDateFormat(
				DateUtil.DATE_FORMAT_8);
		return dateFormator.format(date);

	}

	public static java.sql.Timestamp getTimestamp(String dateStr) {
		java.text.DateFormat df = new SimpleDateFormat(
				DateUtil.DATE_TIME_FORMAT);
		java.sql.Timestamp ts = null;
		try {
			java.util.Date date = df.parse(dateStr);
			ts = new java.sql.Timestamp(date.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ts;
	}

	/**
	 * 将Date转换成统一的日期时间格式文本。
	 * 
	 * @return
	 */
	public static String getFormatedDateTime(java.sql.Date date) {
		if (null == date) {
			return "";
		}

		SimpleDateFormat dateFormator = new SimpleDateFormat(
				DateUtil.DATE_TIME_FORMAT_14);
		return dateFormator.format(new java.sql.Date(date.getTime()));
	}

	/**
	 * 通过统一的格式将文本转换成Date。输入为日期和时间。
	 * 
	 * @return
	 */
	public static java.sql.Date parseDateTime(String sdate) {
		if (null == sdate || "".equals(sdate)) {
			return null;
		}

		// 只有日期类型
		if (sdate.length() <= 10) {
			return parseDate(sdate);
		}

		SimpleDateFormat dateFormator = new SimpleDateFormat(
				DateUtil.DATE_TIME_FORMAT_14);

		java.util.Date tDate = dateFormator.parse(sdate, new ParsePosition(0));
		if (tDate == null) {
			return null;
		}

		return new java.sql.Date(tDate.getTime());

	}

	/**
	 * 通过统一的格式将文本转换成Date。输入为日期。
	 * 
	 * @return
	 */
	public static java.sql.Date parseDate(String sdate) {
		if (null == sdate || "".equals(sdate)) {
			return null;
		}

		SimpleDateFormat dateFormator = new SimpleDateFormat(
				DateUtil.DATE_FORMAT);

		java.util.Date tDate = dateFormator.parse(sdate, new ParsePosition(0));
		if (tDate == null) {
			return null;
		}

		return new java.sql.Date(tDate.getTime());
	}

	/**
	 * 获取14位系统时间
	 * 
	 * @return
	 */
	public static String getDateString14() {
		SimpleDateFormat dateFormator = new SimpleDateFormat("yyyyMMddHHmmss");
		return dateFormator
				.format(new java.sql.Date(System.currentTimeMillis()));
	}

	public static Date add(Date date, int filed, int amount) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(date.getTime());
		c.add(filed, amount);
		return new Date(c.getTimeInMillis());
	}

	public static String current(String pattern) {
		return formatDate(current(), pattern);
	}

	public static Date current() {
		return new java.sql.Date(System.currentTimeMillis());
	}

}
