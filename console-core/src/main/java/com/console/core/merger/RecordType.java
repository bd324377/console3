package com.console.core.merger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum RecordType {
    USER_PROC("USER_PROC"),
    USER_ID("USER_ID");
    @JsonValue
    private final String code;

    RecordType(String code) {
        this.code = code;
    }

    @JsonCreator
    public static RecordType fromCode(String code) {
        for (RecordType recordType : RecordType.values()) {
            if (recordType.code.equals(code)) {
                return recordType;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
