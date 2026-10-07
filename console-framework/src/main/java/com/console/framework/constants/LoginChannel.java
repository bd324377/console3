package com.console.framework.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum LoginChannel {
    WEB(0,"网页端"),
    PWA(1,"App端"),
    APP(2,"PWA端");

    @JsonValue
    @EnumValue
    private final Integer key;
    private final String  value;

    LoginChannel(Integer key,String value) {
        this.key   = key;
        this.value = value;
    }
}
