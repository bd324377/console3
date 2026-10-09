package com.console.payment.channel.strategy.u2c.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QueryOrderReq {
    private String merchantId;
    private String merchantOrderNo;
    private String sign;
}
