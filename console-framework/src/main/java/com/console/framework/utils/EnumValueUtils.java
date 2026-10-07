package com.console.framework.utils;

import com.baomidou.mybatisplus.annotation.EnumValue;

import java.lang.reflect.Field;

public class EnumValueUtils {
    /**
     * 获取枚举中 @EnumValue 标注字段的值
     * 若没有标注，退回 name()
     */
    public static Object getEnumValue(Enum<?> e) {
        if (e == null) {
            return null;
        }
        for (Field field : e.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(EnumValue.class)) {
                field.setAccessible(true);
                try {
                    return field.get(e);
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException("读取 @EnumValue 失败: " + e.getClass(), ex);
                }
            }
        }
        return e.name();
    }
}
