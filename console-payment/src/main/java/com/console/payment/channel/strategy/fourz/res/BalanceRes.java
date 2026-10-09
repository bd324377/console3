package com.console.payment.channel.strategy.fourz.res;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class BalanceRes {
    private BigDecimal balance;
    private BigDecimal unsettledBalance;
    private BigDecimal frozenAmount;
    private String currency;
}
