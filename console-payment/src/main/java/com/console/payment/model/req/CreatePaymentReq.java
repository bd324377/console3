package com.console.payment.model.req;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreatePaymentReq {
    @NotNull
    private Long channelId;
    @NotNull
    private Integer userId;
    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;
    private String payType;
    private String content;
    private String bankCode;
    private String kycPayerIdNo;
    private String kycPayerName;
    private String redirect;
    private String advertId;
}
