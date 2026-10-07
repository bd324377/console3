package com.console.payment.channel.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelResult {
    private boolean success;
    /** 渠道业务错误码或本地归一化的远程调用错误码。 */
    private String errorCode;
    /** 是否建议保留原订单状态并在稍后重新主动查询。 */
    private boolean retryable;
    private String message;
    private String merchantId;
    private String merchantOrderNo;
    private String orderNo;
    private Integer amount;
    private String currency;
    private String status;
    private String payUrl;
    private String payRaw;
}
