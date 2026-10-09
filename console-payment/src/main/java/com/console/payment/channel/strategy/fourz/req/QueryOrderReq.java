package com.console.payment.channel.strategy.fourz.req;

import com.console.payment.utils.PaymentUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryOrderReq {
    private String merchantNo;
    private String merchantOrderNo;
    private String sign;

    public QueryOrderReq(String merchantNo, String merchantOrderNo, String secret) {
        this.merchantNo = merchantNo;
        this.merchantOrderNo = merchantOrderNo;
        sign = PaymentUtils.sign(this, secret);
    }
}
