package com.console.framework.utils;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

public class FieldUtils {
    public static Object getFieldValue(Object object, String fieldName) {
        Field field = ReflectionUtils.findField(object.getClass(), fieldName);
        if (field != null) {
            ReflectionUtils.makeAccessible(field);
            return ReflectionUtils.getField(field, object);
        }
        return null;
    }

    /**
     * 将驼峰命名转换为下划线命名
     * @param fieldName 待转换字段名
     */
    public static String convertToUnderscore(String fieldName) {
        return fieldName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}
