package com.console.framework.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class Result implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Integer code;
    private String  msg;        //异常信息
    private Object  data;


    public Result() {
    }

    public Result(Integer code, String msg, Object data) {
        this.code = code;
        this.msg  = msg;
        this.data = data;
    }

    public Result(Integer status, String msg) {
        this(status, msg, null);
    }

    public static Result success() {
        return new Result(200,null);
    }

    public static Result success(Object object) {
        return new Result(200,null,object);
    }

    public static Result success(Object object,String msg) {
        return new Result(200,msg,object);
    }

    public static Result failed() {
        return new Result(500,null);
    }

    public static Result failed(Integer status) {
        return new Result(status,null);
    }

    public static Result failed(Integer status,String msg) {
        return new Result(status,msg);
    }

    public static Result failed(Integer status,String msg,Object object) {
        return new Result(status,msg,object);
    }

    public static Result error(Exception e) {
        return Result.error(e, null);
    }

    public static Result error(Exception e, Object o) {
        //BusinessException
        if (e instanceof BusinessException businessException) {
            int code = businessException.getCode() == null ? ResultCode.FAILED.getCode() : businessException.getCode().getCode();
            return new Result(code, businessException.getMessage(), o);
        }
        //其他异常类
        return new Result(ResultCode.FAILED.getCode(), e.getMessage(), o);
    }
}
