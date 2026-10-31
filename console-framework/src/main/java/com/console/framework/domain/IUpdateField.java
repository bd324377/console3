package com.console.framework.domain;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

public interface IUpdateField<E> {
    /** 字段名（对应 Req 中的属性名，用于反射取值） */
    String getFieldName();

    /** 实体 getter 引用 */
    SFunction<E, Object> getFunction();

    /** i18n 描述 key */
    String getDescriptionKey();

    /**
     * 根据 fieldName 在枚举中查找字段元数据。
     * 注：接口静态方法只能通过接口名调用，例如 IUpdateField.resolve(UpdateField.class, part)。
     */
    static <E, F extends Enum<F> & IUpdateField<E>> F resolve(Class<F> enumClass, String fieldName) {
        for (F f : enumClass.getEnumConstants()) {
            if (f.getFieldName().equals(fieldName)) {
                return f;
            }
        }
        throw new IllegalArgumentException("不支持的更新字段: " + fieldName);
    }
}
