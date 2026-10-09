package com.console.payment.channel.strategy.fourz.req;

import com.console.payment.utils.PaymentUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryBalanceReq {
    private String merchantNo;
    private String sign;

    public QueryBalanceReq(String merchantNo, String secret) {
        this.merchantNo = merchantNo;
        sign = PaymentUtils.sign(this, secret);
    }
}
