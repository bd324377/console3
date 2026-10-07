package com.console.payment.channel.strategy.u2c.dto.res;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CallBackRes {
    private String merchantId;
    private String merchantOrderNo;
    private String orderNo;
    private Integer amount;
    private Integer paidAmount;
    private String status;
    private String currency;
    private String payType;
    private String ref_cpf;
    private String ref_name;
    private String accountNo;
    private String errorMsg;
    private String sign;
}
