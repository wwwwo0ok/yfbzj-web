package com.company.project.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DateUtil {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATETIME_1_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    // ================== 原有方法 ==================
    public static String getTodayString() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    public static String getDateStringByOffset(int offsetDays) {
        return LocalDate.now().plusDays(offsetDays).format(DATE_FORMATTER);
    }

    // ================== 新增方法 ==================

    /**
     * 将输入的日期时间字符串解析为 LocalDateTime。
     * 兼容 "yyyy-MM-dd" 和 "yyyy-MM-dd HH:mm:ss" 格式。
     *
     * @param dateTimeStr 日期时间字符串
     * @return LocalDateTime 对象
     * @throws DateTimeParseException 如果格式无法解析
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            throw new IllegalArgumentException("日期字符串不能为空");
        }
        dateTimeStr = dateTimeStr.trim();
        // 尝试解析完整日期时间格式
        try {
            return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            // 如果失败，尝试仅解析日期部分，并补充时间为 00:00:00
            try {
                LocalDate date = LocalDate.parse(dateTimeStr, DATE_FORMATTER);
                return date.atStartOfDay();
            } catch (DateTimeParseException ex) {
                throw new DateTimeParseException("无法解析日期字符串，请使用 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss 格式", dateTimeStr, 0, ex);
            }
        }
    }

    /**
     * 传入日期字符串（兼容时分秒），返回该日期的 00:00:00 字符串（格式 yyyy-MM-dd HH:mm:ss）
     *
     * @param dateTimeStr 输入的日期字符串，例如 "2014-12-26" 或 "2014-12-26 12:34:56"
     * @return 该日期的零点字符串，例如 "2014-12-26 00:00:00"
     */
    public static String getStartOfDayString(String dateTimeStr) {
        LocalDateTime dateTime = parseDateTime(dateTimeStr);
        // 取日期部分，然后置为当天零点
        LocalDateTime startOfDay = dateTime.toLocalDate().atStartOfDay();
        return startOfDay.format(DATETIME_FORMATTER);
    }

    /**
     * 传入日期字符串，返回该日期的 00:00:00 对应的时间戳（毫秒）
     *
     * @param dateTimeStr 日期字符串
     * @return 毫秒时间戳
     */
    public static long getStartOfDayTimestamp(String dateTimeStr) {
        LocalDateTime startOfDay = parseDateTime(dateTimeStr).toLocalDate().atStartOfDay();
        return startOfDay.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取今天的 23:59:59 字符串（格式 yyyy-MM-dd HH:mm:ss）
     *
     * @return 例如 "2025-01-15 23:59:59"
     */
    public static String getEndOfTodayString() {
        LocalDateTime endOfToday = LocalDate.now().atTime(LocalTime.MAX);
        return endOfToday.format(DATETIME_FORMATTER);
    }

    /**
     * 获取今天的 23:59:59 对应的时间戳（毫秒）
     *
     * @return 毫秒时间戳
     */
    public static long getEndOfTodayTimestamp() {
        LocalDateTime endOfToday = LocalDate.now().atTime(LocalTime.MAX);
        return endOfToday.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
    
    /**
     * 传入日期字符串（兼容时分秒），返回该日期的 23:59:59 字符串（格式 yyyy-MM-dd HH:mm:ss）
     *
     * @param dateTimeStr 输入的日期字符串，例如 "2014-12-26" 或 "2014-12-26 12:34:56"
     * @return 该日期的结束时间字符串，例如 "2014-12-26 23:59:59"
     */
    public static String getEndOfDayString(String dateTimeStr) {
        LocalDateTime dateTime = parseDateTime(dateTimeStr);
        LocalDateTime endOfDay = dateTime.toLocalDate().atTime(LocalTime.MAX);
        return endOfDay.format(DATETIME_FORMATTER);
    }

    /**
     * 传入日期字符串，返回该日期的 23:59:59 对应的时间戳（毫秒）
     *
     * @param dateTimeStr 日期字符串
     * @return 毫秒时间戳
     */
    public static long getEndOfDayTimestamp(String dateTimeStr) {
        LocalDateTime endOfDay = parseDateTime(dateTimeStr).toLocalDate().atTime(LocalTime.MAX);
        return endOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    // ================== 互转方法 ==================

    /**
     * 将字符串（yyyy-MM-dd）转换为 LocalDate
     *
     * @param dateStr 日期字符串，如 "2024-12-25"
     * @return LocalDate
     */
    public static LocalDate parseToLocalDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    /**
     * 将 LocalDate 转换为字符串（yyyy-MM-dd）
     *
     * @param date LocalDate
     * @return 格式化的日期字符串
     */
    public static String formatLocalDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    /**
     * 将字符串（yyyy-MM-dd HH:mm:ss）转换为 LocalDateTime
     *
     * @param dateTimeStr 日期时间字符串
     * @return LocalDateTime
     */
    public static LocalDateTime parseToLocalDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }

    /**
     * 将 LocalDateTime 转换为字符串（yyyy-MM-dd HH:mm:ss）
     *
     * @param dateTime LocalDateTime
     * @return 格式化的日期时间字符串
     */
    public static String formatLocalDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    /**
     * 将日期时间字符串转换为对应的时间戳（毫秒）
     * 兼容 "yyyy-MM-dd" 和 "yyyy-MM-dd HH:mm:ss" 格式
     *
     * @param dateTimeStr 日期时间字符串，例如 "2014-12-26" 或 "2014-12-26 12:34:56"
     * @return 毫秒时间戳
     */
    public static long getTimestamp(String dateTimeStr) {
        LocalDateTime dateTime = parseDateTime(dateTimeStr);
        return dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
    
    /**
     * 判断两个日期之间的天数差是否不超过限制天数（基于日期部分，忽略时间）
     * 
     * @param startDateStr 起始日期字符串，兼容 "yyyy-MM-dd" 或 "yyyy-MM-dd HH:mm:ss"
     * @param endDateStr   结束日期字符串，兼容 "yyyy-MM-dd" 或 "yyyy-MM-dd HH:mm:ss"
     * @param limitDays    限制天数（正数）
     * @return true 表示两个日期间隔天数 ≤ limitDays（未超过限制）；false 表示间隔天数 > limitDays（超过限制）
     * @throws IllegalArgumentException 如果日期字符串解析失败
     */
    public static boolean isWithinDays(String startDateStr, String endDateStr, int limitDays) {
        if (limitDays < 0) {
            throw new IllegalArgumentException("limitDays 不能为负数");
        }
        LocalDate startDate = parseDateTime(startDateStr).toLocalDate();
        LocalDate endDate = parseDateTime(endDateStr).toLocalDate();
        long daysBetween = Math.abs(ChronoUnit.DAYS.between(startDate, endDate));
        return daysBetween <= limitDays;
    }
    /**
     * 获取昨天的日期字符串，格式：yyyy-MM-dd
     * @return 例如 2024-12-24（如果今天是2024-12-25）
     */
    public static String getYesterdayString() {
        return LocalDate.now().minusDays(1).format(DATE_FORMATTER);
    }
    
    /**
     * 获取从开始日期到结束日期的日期字符串列表（包含首尾）
     * @param startDate 开始日期，格式 yyyy-MM-dd
     * @param endDate   结束日期，格式 yyyy-MM-dd
     * @return 日期字符串列表，例如 ["2024-12-01", "2024-12-02", "2024-12-03"]
     * @throws IllegalArgumentException 如果开始日期晚于结束日期
     */
    public static List<String> getDateRangeList(String startDate, String endDate) {
        LocalDate start = parseToLocalDate(startDate);
        LocalDate end = parseToLocalDate(endDate);
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }
        List<String> dateList = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            dateList.add(date.format(DATE_FORMATTER));
        }
        return dateList;
    }

    /**
     * 获取从开始日期到结束日期的日期字符串数组（包含首尾）
     * @param startDate 开始日期，格式 yyyy-MM-dd
     * @param endDate   结束日期，格式 yyyy-MM-dd
     * @return 日期字符串数组
     */
    public static String[] getDateRangeArray(String startDate, String endDate) {
        List<String> list = getDateRangeList(startDate, endDate);
        return list.toArray(new String[0]);
    }
    
    /**
     * 从指定日期开始（包含），往回倒数 days 天，返回包含起始日期在内的 days 个日期字符串（升序）
     * @param startDate 起始日期，格式 yyyy-MM-dd
     * @param days      要获取的天数（包含起始日，>=1）
     * @return 日期字符串数组，按从早到晚排序，例如 startDate=2024-12-25, days=3 -> ["2024-12-23", "2024-12-24", "2024-12-25"]
     * @throws IllegalArgumentException 如果 days < 1 或 startDate 无效
     */
    public static String[] getLastDaysFromDate(String startDate, int days) {
        if (days < 1) {
            throw new IllegalArgumentException("天数必须大于等于1");
        }
        LocalDate start = parseToLocalDate(startDate);
        LocalDate earliest = start.minusDays(days - 1);
        List<String> result = new ArrayList<>();
        for (LocalDate date = earliest; !date.isAfter(start); date = date.plusDays(1)) {
            result.add(date.format(DATE_FORMATTER));
        }
        return result.toArray(new String[0]);
    }

    /**
     * 包含今天在内，往回倒数 days 天，返回数组（共 days 个日期，按从早到晚排序）
     * @param days 要获取的天数（包含今天，>=1）
     * @return 日期字符串数组，例如今天是 2024-12-25，days=3 -> ["2024-12-23", "2024-12-24", "2024-12-25"]
     */
    public static String[] getLastDaysFromToday(int days) {
        return getLastDaysFromDate(getTodayString(), days);
    }
    
    /**
     * 获取其他日期，1则是明天，-1则是昨天。
     * @param dayString
     * @param dayInt
     * @return
     */
    public static String getTheNextDayString(String dayString, int dayInt) {
        LocalDateTime dateTime = parseDateTime(dayString);
        LocalDateTime nextDay = dateTime.plusDays(dayInt);
        return nextDay.toLocalDate().toString(); // yyyy-MM-dd
    }
}