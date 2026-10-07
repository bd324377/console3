package com.console.framework.domain;

import com.console.framework.domain.ResultCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    private ResultCode code;
    private String  message;
    private Object  date;

    public BusinessException(Integer code, String message) {
        this(code,message,null);
    }

    public BusinessException(Integer code, String message, Object date){
        super(message);
        this.code    = ResultCode.fromCode(code);
        this.message = message;
        this.date    = date;
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.code    = resultCode;
        this.message = resultCode.getMsg();
    }
}
