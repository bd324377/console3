package com.console.framework.utils;

import lombok.experimental.UtilityClass;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

@UtilityClass
public class DateUtils {
    /** 昨日日期 */
    public static LocalDate yesterday() {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1);
    }

    /** 上周周一 */
    public static LocalDate lastWeekMonday() {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1);
    }

    /** 上周周日 */
    public static LocalDate lastWeekSunday() {
        return lastWeekMonday().plusDays(6);
    }

    /** 上月第一天 */
    public static LocalDate lastMonthFirstDate() {
        return LocalDate.now().minusMonths(1).withDayOfMonth(1);
    }

    /** 上月最后一天 */
    public static LocalDate lastMonthLastDate() {
        return LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
    }

    /** 本周周一 */
    public static LocalDate thisWeekFirstDate() {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }
    /** 本周周日 */
    public static LocalDate thisWeekLastDate() {
        return thisWeekFirstDate().plusDays(6);
    }

    /** 本月第一天 */
    public static LocalDate thisMonthFirstDate() {
        return LocalDate.now().withDayOfMonth(1);
    }
    /** 本月最后一天 */
    public static LocalDate thisMonthLastDate() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }

    /** 闭区间 [start, end]，支持跨天 */
    public static boolean isBetween(LocalTime time, LocalTime start, LocalTime end) {
        if (time == null || start == null || end == null) return false;
        if (start.equals(end)) return true; // 起止相同视为全天
        if (start.isAfter(end)) {
            return !time.isBefore(start) || !time.isAfter(end);
        }
        return !time.isBefore(start) && !time.isAfter(end);
    }

    public static boolean isToday(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        return dateTime.toLocalDate().isEqual(LocalDate.now());
    }

    public static boolean isBeforeYesterday(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        LocalDateTime startOfYesterday = LocalDate.now().minusDays(1).atStartOfDay();
        return dateTime.isBefore(startOfYesterday);
    }

    public static int daysBetweenByTime(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("时间不能为空");
        }
        return (int) ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate());
    }

    /**
     * 获取当前时间戳
     */
    public Integer getCurrentTimestamp() {
        return (int) LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    /**
     * 获取当前时间戳(毫秒)
     */
    public Long getCurrentTimestampOfMs() {
        return LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取今日时间戳
     */
    public int getTodayTimestamp() {
        return (int) LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
    }

    /**
     * 将对应时间转为时间错
     */
    public int getTimestampByLocalDateTime(LocalDateTime localDateTime,String timeZone) {
        return (int) localDateTime.atZone(ZoneId.of(timeZone)).toEpochSecond();
    }

    public int getTimestampByLocalDateTime(LocalDateTime localDateTime) {
        return (int) localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    /**
     * 获取当前时间到目的时间的倒计时（秒）
     * @param targetTime 目的时间
     */
    public long getCountdown(Integer targetTime) {
        Instant currentTimestamp = Instant.now();
        Instant instant = Instant.ofEpochSecond(targetTime);
        return ChronoUnit.SECONDS.between(currentTimestamp, instant);
    }

    /**
     * 获取当前时间到目的时间的倒计时（秒）
     * @param startTime 目的时间
     * @param targetTime 目的时间
     */
    public long getCountdown(Integer startTime,Integer targetTime) {
        Instant start  = Instant.ofEpochSecond(startTime);
        Instant target = Instant.ofEpochSecond(targetTime);
        return ChronoUnit.SECONDS.between(start, target);
    }
}
