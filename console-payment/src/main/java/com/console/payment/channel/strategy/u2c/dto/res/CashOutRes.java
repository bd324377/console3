package com.console.payment.channel.strategy.u2c.dto.res;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CashOutRes {
    private String merchantId;
    private String merchantOrderNo;
    private String orderNo;
    private Integer amount;
    private String status;
    private String currency;
    private String errorMsg;
}
