package com.console.framework.domain;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "参数或业务状态错误"),
    FAILED(500, "操作失败"),
    AUTHENTICATION_FAILED(401, "验证失败"),
    UNAUTHORIZED_FAILED(403, "授权失败"),
    NOT_FOUND(404, "不存在"),
    LOCK_NOT_RELEASED(1000, "distributed transaction lock not unlocked correctly"),//分布式锁未正确解锁
    PARAMS_ERROR(1001, "参数错误"),
    PASSWORD_ERROR(1002, "密码错误，请重新输入");
    private final Integer code;
    private final String msg;
    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ResultCode fromCode(int code) {
        for (ResultCode resultCode : ResultCode.values()) {
            if (resultCode.code == code) {
                return resultCode;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
