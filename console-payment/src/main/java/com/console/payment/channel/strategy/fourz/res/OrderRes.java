package com.console.payment.channel.strategy.fourz.res;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRes {
    private String merchantNo;
    private String merchantOrderNo;
    private String orderNo;
    /** FourZ 返回字符串格式的整数分；验签前保持原始文本。 */
    private String amount;
    private String status;
    private String currency;
}
