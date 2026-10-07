package com.console.payment.channel.strategy.u2c.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryBalanceReq {
    private String  merchantId; // 商户ID
    private String  sign;       // 签名
}
