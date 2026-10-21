package com.console.framework.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum CaptchaType {
    BIND_PHONE(0,"绑定手机"),
    BINGD_EMAIL(1,"绑定邮箱"),
    CASH_OUT(2,"首次提现");

    @JsonValue
    @EnumValue
    private final Integer key;
    private final String  value;

    CaptchaType(Integer key,String value) {
        this.key   = key;
        this.value = value;
    }
}
