package com.console.payment.channel.strategy.fourz.res;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FourZResponse<T> {
    private boolean success;
    private String errorCode;
    private String message;
    private T data;
}
