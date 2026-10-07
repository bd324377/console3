package com.console.framework.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Locale {
    EN_US("en-US", "英语（美国）"),
    PT_BR("pt-BR", "葡萄牙语（巴西）"),
    ZH_CN("zh-CN", "简体中文（中国）"),
    ZH_TW("zh-TW", "繁体中文（台湾）");
    @JsonValue
    private final String code;
    private final String displayName;

    Locale(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    @JsonCreator
    public static Locale fromCode(String code) {
        for (Locale lang : Locale.values()) {
            if (lang.code.equals(code)) {
                return lang;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }

    public String getLanguageCode() {
        String[] parts = code.split("-");
        if (parts.length > 1) {
            return parts[0];
        }
        return "";
    }
}
