package com.console.payment.channel.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentCommand {
    private String payType;
    private String content;
    private String bankCode;
    private String kycPayerIdNo;
    private String kycPayerName;
    private String clientIp;
    private String redirect;
}
