package com.console.payment.model.req;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateCashOutReq {
    @NotNull
    private Long channelId;
    @NotNull
    private Integer userId;
    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;
    @NotBlank
    private String accountType;
    @NotBlank
    private String accountNo;
    @NotBlank
    private String accountName;
    private String bankCode;
    private String branchBankNo;
    private String branchBankName;
    private String accountMobile;
    private String accountEmail;
    private String province;
    private String city;
    private String cpf;
    private String ifsc;
}
