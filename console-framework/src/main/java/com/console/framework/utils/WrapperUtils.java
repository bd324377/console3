package com.console.framework.utils;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

public class WrapperUtils {
    /**
     * 给更新语句复制
     * @param updateWrapper     跟新语句wrapper
     * @param propertyGetter    更新字段
     * @param value             更新内容
     */
    public static <T> void setPropertyValue(UpdateWrapper<T> updateWrapper, SFunction<T, Object> propertyGetter, Object value) {
        if (value == null) {
            updateWrapper.lambda().set(propertyGetter, null);
        } else {
            if (value instanceof String || value instanceof Integer) {
                updateWrapper.lambda().set(propertyGetter, value);
            } else {
                updateWrapper.lambda().set(propertyGetter, JSON.toJSONString(value));
            }

        }
    }
}
