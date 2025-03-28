package com.ukayunnuo.demo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

/**
 * @author yunnuo
 * @since 1.0.0
 */
public class DateTest {
    public static void main(String[] args) {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();

        // 获取上个月的第一天
        LocalDate firstDayOfLastMonth = currentDate.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());

        // 定义日期格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 格式化日期为字符串
        String formattedDate = firstDayOfLastMonth.format(formatter);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        // 输出结果
        System.out.println("上个月的第一天: " + formattedDate);
        System.out.println("上个月的第一天: " + sdf.format(getFirstDayOfMonth(new Date())));

        LocalDate lastDayOfLastMonth = currentDate.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        String formattedLastDay = lastDayOfLastMonth.format(formatter);

        System.out.println("上个月的最后一天: " + formattedLastDay);
        System.out.println("上个月的最后一天: " + sdf.format(getEndDayOfMonth(new Date())));
    }

    /**
     * 获取当前时间的当月第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfMonth(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date result = null;
        try {
            String dateStr = sdf.format(calendar.getTime());
            result = sdf.parse(dateStr);
        } catch (ParseException e) {
        }
        return result;
    }

    /**
     * 获取当前时间的当月最后一天
     *
     * @param date
     * @return
     */
    public static Date getEndDayOfMonth(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        Date result = null;
        try {
            String dateStr = sdf.format(calendar.getTime());
            result = sdf.parse(dateStr);
            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(result.getTime()), ZoneId.systemDefault());
            LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
            result = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
        } catch (ParseException e) {
        }
        return result;
    }
}
