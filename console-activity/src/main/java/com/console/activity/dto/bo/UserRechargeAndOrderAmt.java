package com.console.activity.dto.bo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UserRechargeAndOrderAmt {
    private BigDecimal rechargeAmt = BigDecimal.ZERO;
    private BigDecimal orderAmt = BigDecimal.ZERO;
    private BigDecimal orderResultAmt = BigDecimal.ZERO;
}
