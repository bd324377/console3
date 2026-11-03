package com.console.payment.channel.strategy.test.res;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestCallBackRes {
    private String amount;//金额
    private String merchantId;//商户ID
    private String merchantOrderNo;//商户订单号
    private String sign;    //签名
}
