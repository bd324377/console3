package com.console.payment.channel.strategy.utc.model;

import lombok.Getter;
import lombok.Setter;

/**
 * u to c 渠道请求结果类
 */
@Getter
@Setter
public class UTCResponse<T> {
    private boolean success;//true 成功,false 失败
    private String  errorCode;//错误编码:成功-SUCCESS
    private String  message;//错误信息
    private T data;
}
