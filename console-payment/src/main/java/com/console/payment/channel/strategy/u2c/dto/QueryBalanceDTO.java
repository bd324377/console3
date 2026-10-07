package com.console.payment.channel.strategy.u2c.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.TreeMap;

@Getter
@Setter
public class QueryBalanceDTO {
    private String  merchantId; // 商户ID
    private String  sign;       // 签名
//    public void buildSign(String apiSecret) {
//        TreeMap<String, Object> paramsMap = PaymentUtils.convert(this, true, false);
//        this.sign = PaymentUtils.serialMd5Sign(paramsMap,apiSecret);
//    }
}
