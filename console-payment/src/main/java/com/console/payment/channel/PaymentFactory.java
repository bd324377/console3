package com.console.payment.channel;

import com.console.payment.entity.PaymentChannel;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 支付策略工厂。Spring 会以 Bean 名称为 key 注入所有渠道策略，
 * {@link PaymentChannel#getBelongClass()} 必须与对应策略的 Bean 名称一致。
 */
@Component
public class PaymentFactory {
    private final Map<String, PaymentStrategy> strategies;

    public PaymentFactory(Map<String, PaymentStrategy> strategies) {
        this.strategies = strategies;
    }

    public PaymentStrategy getStrategy(PaymentChannel channel, boolean cashOut) {
        // 在选择策略前统一校验渠道存在性及充值/提现开关。
        if (channel == null) {
            throw new IllegalArgumentException("payment channel does not exist");
        }
        if (cashOut ? !Integer.valueOf(1).equals(channel.getCashOutStatus())
                : !Integer.valueOf(1).equals(channel.getPayStatus())) {
            throw new IllegalStateException("payment channel is disabled");
        }
        PaymentStrategy strategy = strategies.get(channel.getBelongClass());
        if (strategy == null) {
            throw new IllegalArgumentException("unknown payment strategy: " + channel.getBelongClass());
        }
        return strategy;
    }
}
