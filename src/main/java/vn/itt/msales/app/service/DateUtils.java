/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.app.service;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author vtm
 */
public class DateUtils {

    public static Date getLongNow() {
        return new Date();
    }

    public static Date getShortNow() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getShortDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getMondayOfWeek() {
        return getMonday(new Date());
    }

    public static Date getSundayOfWeek() {
        return getSunday(new Date());
    }

    public static String getDayString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int thuIdx = calendar.get(Calendar.DAY_OF_WEEK);
        if (thuIdx == 1) {
            return "Chủ nhật";
        } else {
            return "Thứ " + thuIdx;
        }
    }

    public static Date getNextDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static Date getBackDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }

    public static Date getFirstDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return getShortDate(calendar.getTime());
    }

    public static Date getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return getShortDate(calendar.getTime());
    }

    public static Date getFirstDayOfMonth(int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return getShortDate(calendar.getTime());
    }

    public static Date getLastDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return getShortDate(calendar.getTime());
    }

    public static Date getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return getShortDate(calendar.getTime());
    }

    public static Date getLastDayOfMonth(int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return getShortDate(calendar.getTime());
    }

    public static long subDate(Date date1, Date date2) {
        long diff = Math.abs(date1.getTime() - date2.getTime());
        return (diff / (24 * 60 * 60 * 1000)) + 1;
    }

    public static Date addSecond(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, amount);
        return calendar.getTime();
    }

    public static Date addDay(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, amount);
        return calendar.getTime();
    }

    public static Date getMonday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int thu = calendar.get(Calendar.DAY_OF_WEEK);
        if (thu == 1)//chu nhat
        {
            thu = 8;
        }
        thu = thu - 2;
        calendar.add(Calendar.DAY_OF_MONTH, 0 - thu);
        Date minDate = calendar.getTime();
        return DateUtils.getShortDate(minDate);
    }

    public static Date getSunday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int thu = calendar.get(Calendar.DAY_OF_WEEK);
        if (thu == 1)//chu nhat
        {
            thu = 8;
        }
        thu = thu - 2;
        calendar.add(Calendar.DAY_OF_MONTH, 0 - thu);
        calendar.add(Calendar.DAY_OF_MONTH, 6);
        Date maxDate = calendar.getTime();
        return DateUtils.getShortDate(maxDate);
    }
}
