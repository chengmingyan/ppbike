package com.ppbike.util;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class TimeUtil {
//	private final String1 //中国时区的信息
//    DEFAULT_TIME_ZONE_INFORMATION = {-480};
	private static final String TAG = TimeUtil.class.getName();
	
	public static final String LOCAL_TIME_PATTERN_STRING = "yyyy-MM-dd";
	public static final String LOCAL_TIME_WITHOUT_YEAR_PATTERN_STRING = "MM-dd";
	public static final String WEEK_HOUR_MINUTE = "aHH:mm";
	public static final String LOCAL_TIME_WITHOUT_HOURSE_M = "HH:mm";
	public static final String LOCAL_DETAIL_TIME_PATTERN_STRING = "yyyy-MM-dd HH:mm";
	public static final String LOCAL_DETAIL_TIME_PATTERN_STRING_S = "yyyy-MM-dd HH:mm:ss";
	public static final String LOCAL_DETAIL_TIME_WITHOUT_YEAR_PATTERN_STRING = "MM-dd HH:mm";
	/**增加或减少天数获取改变后时间数组
	 * @param addDayValues
	 * 正数：增加天数，负数：减少天数
	 * @return
	 * String1 [5]
	 * 0,year;1,month;2,day;3,hour;4,minute
	 */
	public static int[] getDateByChangDay(int addDayValues){
		int[] time = new int[5];
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, addDayValues);
		time[0] = calendar.get(Calendar.YEAR);
		time[1] = calendar.get(Calendar.MONTH);
		time[2] = calendar.get(Calendar.DAY_OF_MONTH);
		time[3] = calendar.get(Calendar.HOUR_OF_DAY);
		time[4] = calendar.get(Calendar.MINUTE);
		return time;
	}
	
	/**
	 * @param localTime
	 * @return
	 */
	public static String localTime2utc(String localTime) { 
		String utcTimeString = "";
		if (localTime!=null && localTime.length() > 0) {
			SimpleDateFormat localFormater = new SimpleDateFormat(LOCAL_TIME_PATTERN_STRING); 
			localFormater.setTimeZone(TimeZone.getDefault());
			Date date;
			try{
				date = localFormater.parse(localTime);
			}catch (Exception e) {
				// TODO: handle exception
				Log.e(TAG, "local time:" + localTime + " is invalid time string");
				return "";
			}
			long utcTimes = dateToUtcTime(date);
			utcTimeString = String.valueOf(utcTimes);
		}
		return utcTimeString; 
	}
	
	/**固定格式的本地时间转UTC时间
	 * @param localTime
	 * 本地时间 
	 * @param dateFormat
	 * 时间格式
	 * @return
	 * UTC毫秒数
	 */
	public static long localTime2utcTime(String localTime,String dateFormat) { 
		long utcTimes = 0;
		if (localTime!=null && localTime.length() > 0) {
			SimpleDateFormat localFormater = new SimpleDateFormat(dateFormat); 
			localFormater.setTimeZone(TimeZone.getDefault());
			Date date;
			try{
				date = localFormater.parse(localTime);
				utcTimes = dateToUtcTime(date);
			}catch (Exception e) {
				Log.e(TAG, "local time:" + localTime + " is invalid time string");
				return utcTimes;
			}
		}else {
			new Exception("Date1 is not null");
		}
		return utcTimes; 
	}
	/**将本地当前时间转换成UTC国际标准时间的毫秒形式
	 * @param date
	 * 本地时间
	 * @return
	 * UTC国际标准时间的毫秒形式
	 */
	public static long dateToUtcTime(Date date){
        if(date != null){   
        	Calendar calendar = Calendar.getInstance();
    		calendar.setTime(date);
    		return calendar.getTimeInMillis();
        }else{
        	return 0;
        }
	}
	/**@description:将utc时间(毫秒)数转换为date时间类型
	 * @param utcTime
	 * utc时间(毫秒)
	 * @return
	 * date时间
	 */
	public static Date utcTimeToDate(long utcTime){
		Date resultDate = null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(utcTime);
		resultDate = calendar.getTime();
		return resultDate;
	}


	/**获取某个月的天数
	 * @param year
	 * @param month
	 * @return
	 * 某个月的天数
	 */
	public static int getDayNumByYearMonth(int year, int month) { 

		int monthInt = month;
		int yearInt = year;
		switch (monthInt) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			return 31;
		case 2:
			if ((yearInt % 400) == 0 ||
				(((yearInt % 100) != 0) && ((yearInt %4) == 0))){
				return 29;
			}else{
				return 28;
			}
		default:
			return 30;
		}
	}

	/**将UTC时间转换为中国时区的本地时间 
	 * @param utcTime
	 * @param dateFormat
	 * @return
	 */
	public static String utcToNativeTime(long utcTime,String dateFormat) { 

		String localTime = "";
	     //中国时区的信息
	     final long TIME = 8*60*60*1000;
	     //将UTC时间转换为中国时区的本地时间
	     utcTime -= TIME;
		if (utcTime > 0) {
			Date localDate = utcTimeToDate(utcTime);
			SimpleDateFormat localFormater = new SimpleDateFormat(dateFormat); 
			localFormater.setTimeZone(TimeZone.getDefault()); 
			localTime = localFormater.format(localDate); 
		}
		return localTime; 
	}
	/**将UTC时间转换为本地时间
	 * @param utcTime
	 * @param dateFormat
	 * @return
	 */
	public static String utc2Local(long utcTime,String dateFormat) { 
		String localTime = "";
		if (utcTime > 0) {
			Date localDate = utcTimeToDate(utcTime);
			SimpleDateFormat localFormater = new SimpleDateFormat(dateFormat); 
			localFormater.setTimeZone(TimeZone.getDefault()); 
			localTime = localFormater.format(localDate); 
		}
		return localTime; 
	}

	/**
	 * 判断时间A是否在时间B之前
	 * 
	 * @param dateStart
	 *            时间A
	 * @param dateEnd
	 *            时间B
	 * @return 返回true，表示时间A在时间B之前
	 * @throws ParseException
	 */
	public static boolean dateABeforeDateB ( String dateStart, String dateEnd )
			throws ParseException {
		System.out.println("start:" + dateStart);
		System.out.println("end:" + dateEnd);

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//定义时间格式
		Date start = df.parse(dateStart);
		Date end = df.parse(dateEnd);
		return start.before(end);
	}
	/**系统时间转本地时间
	 * @param utcTime
	 * @param format
	 * @return
	 * 某格式的String型时间串
	 */
	public static String systemTime2LocalTime(long utcTime,String format) { 
		String localTime = "";
		if (utcTime > 0) {
			SimpleDateFormat localFormater = new SimpleDateFormat(format); 
			localTime = localFormater.format(utcTime); 
		}
		return localTime; 
	}
	public static final long  ONE_HOUR_TIMES = 1000 * 60 * 60 ;
	public static final long  ONE_DAY_TIMES = 1000 * 60 * 60 * 24;
	public static final long ONE_WEEK_TIMES = 7*ONE_DAY_TIMES;
	public static String obtainLastUpdateTimeStatuString( long time){
		if (0 >= time) {
			return "未更新";
		}

		if (System.currentTimeMillis() - time < 10*60 * 1000) {
			return "刚刚";
		} else if (System.currentTimeMillis()
				- time < 20 * 60 * 1000) {
			return 10 +"分钟前";
		}else if(System.currentTimeMillis()
				- time < 30 * 60 * 1000){
			return 20+"分钟前";
		}else if(System.currentTimeMillis()
				- time <ONE_HOUR_TIMES){
			return "半小时前";}
		else if (System.currentTimeMillis()
				- time < ONE_DAY_TIMES) {
			return (System.currentTimeMillis()-time)/ONE_HOUR_TIMES+"小时前";
		}
		else if (System.currentTimeMillis()
				-  time   < ONE_WEEK_TIMES) {
			return (System.currentTimeMillis()- time )/ONE_DAY_TIMES+"天前" ;
		}
		Date curr = new Date();
		Date old = new Date( time );
		if (curr.getYear()==old.getYear()) {
			return TimeUtil.utc2Local(time, "MM月dd日");
		}
		return TimeUtil.utc2Local(time,"yyyy年MM月dd日");
	}
}
