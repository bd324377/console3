package com.console.payment.channel.strategy.u2c.res;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BalanceRes {
    private BigDecimal balance;         //账户余额
    private BigDecimal unsettledBalance;//待结算金额
    private BigDecimal frozenAmount;    //冻结金额
    private String     currency;        //币种
}
