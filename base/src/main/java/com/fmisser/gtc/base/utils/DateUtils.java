package com.fmisser.gtc.base.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期相关
 */

public class DateUtils {

    // 根本日期计算年龄
    public static int getAgeFromBirth(Date birth) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime birthDay = birth.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return now.getYear() - birthDay.getYear() - 1;
    }

    // 根据日期计算星座
    public static String getConstellationFromBirth(Date birth) {
        final int[] dayArr = new int[] { 20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22 };
        final String[] constellationArr = new String[] { "摩羯座", "水瓶座", "双鱼座", "白羊座",
                "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座" };

        LocalDateTime birthDay = birth.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        int month = birthDay.getMonthValue();
        int day = birthDay.getDayOfMonth();
        return day < dayArr[month - 1] ? constellationArr[month - 1] : constellationArr[month];
    }

    // 获取给定时间的当前小时开始的时间
    public static LocalDateTime getHourStart(LocalDateTime dateTime) {
        return LocalDateTime.of(dateTime.getYear(),
                dateTime.getMonth(),
                dateTime.getDayOfMonth(),
                dateTime.getHour(),
                0, 0, 0);
    }

    // 获取给定时间的当前小时结束的时间
    public static LocalDateTime getHourEnd(LocalDateTime dateTime) {
        return getHourStart(dateTime).plusHours(1).plusNanos(-1);
    }

    public static Date getHourStart(Date date) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        gregorianCalendar.set(Calendar.MINUTE, 0);
        gregorianCalendar.set(Calendar.SECOND, 0);
        gregorianCalendar.set(Calendar.MILLISECOND, 0);
        return gregorianCalendar.getTime();
    }

    public static Date getDayStart(Date date) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        gregorianCalendar.set(Calendar.HOUR_OF_DAY, 0);
        gregorianCalendar.set(Calendar.MINUTE, 0);
        gregorianCalendar.set(Calendar.SECOND, 0);
        gregorianCalendar.set(Calendar.MILLISECOND, 0);
        return gregorianCalendar.getTime();
    }

    public static Date getDayEnd(Date date) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        gregorianCalendar.set(Calendar.HOUR_OF_DAY, 23);
        gregorianCalendar.set(Calendar.MINUTE, 59);
        gregorianCalendar.set(Calendar.SECOND, 59);
        gregorianCalendar.set(Calendar.MILLISECOND, 999);
        return gregorianCalendar.getTime();
    }

    public static Date getHourEnd(Date date) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
//        int hour = gregorianCalendar.get(Calendar.HOUR_OF_DAY);
//        gregorianCalendar.set(Calendar.HOUR_OF_DAY, hour + 1);
        gregorianCalendar.set(Calendar.MINUTE, 59);
        gregorianCalendar.set(Calendar.SECOND, 59);
        gregorianCalendar.set(Calendar.MILLISECOND, 999);
        return gregorianCalendar.getTime();
    }
}
