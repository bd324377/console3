package com.console.framework.utils;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@UtilityClass
public class DateUtils {
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
