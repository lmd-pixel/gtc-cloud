package com.fmisser.gtc.base.utils;

import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

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
//获取当天是周几 1=周日 2=周一 3=周二 4=周三 5=周四 6=周五 7=周六
    public static int getWeek(){
        Date today = new Date();
        Calendar c=Calendar.getInstance();
        c.setTime(today);
        int weekday=c.get(Calendar.DAY_OF_WEEK);
        return weekday;
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

    // 判断时分秒是否在某个时分秒时间段内(跨天)
    @SneakyThrows
    public static boolean isTimeBetween(Date time, Date startTime, Date endTime) {

        if (Objects.isNull(time) || Objects.isNull(startTime) || Objects.isNull(endTime)) {
            throw new NullPointerException();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        time = dateFormat.parse(dateFormat.format(time));
        startTime = dateFormat.parse(dateFormat.format(startTime));
        endTime = dateFormat.parse(dateFormat.format(endTime));

        Date dayEnd = dateFormat.parse("23:59:59");
        Date dayStart = dateFormat.parse("00:00:00");

        if (startTime.before(endTime)) {
            // 不超过24点
            if ((time.after(startTime) || time.equals(startTime)) &&
                    (time.before(endTime) || time.equals(endTime))) {
                return true;
            }
        } else {
            // 超过24点
            if ((time.after(startTime) || time.equals(startTime) && (time.before(dayEnd) || time.equals(dayEnd))) ||
                    (time.after(dayStart) || time.equals(dayStart)) && (time.before(endTime) || time.equals(endTime))) {
                return true;
            }
        }

        return false;
    }






}
