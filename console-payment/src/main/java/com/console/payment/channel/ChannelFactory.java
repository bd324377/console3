package com.console.payment.channel;

import com.console.payment.channel.strategy.ChannelStrategy;
import com.console.payment.entity.Channel;
import com.console.payment.service.ChannelService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 支付策略工厂。Spring 会以 Bean 名称为 key 注入所有渠道策略，
 * {@link Channel#getBelongClass()} 必须与对应策略的 Bean 名称一致。
 */
@Component
public class ChannelFactory {
    @Resource
    private ChannelService channelService;
    private final Map<String, ChannelStrategy> strategies;

    public ChannelFactory(Map<String, ChannelStrategy> strategies) {
        this.strategies = strategies;
    }

    public ChannelStrategy getStrategy(Channel channel, boolean cashOut) {
        // 在选择策略前统一校验渠道存在性及充值/提现开关。
        if (channel == null) {
            throw new IllegalArgumentException("payment channel does not exist");
        }
        if (cashOut ? !Integer.valueOf(1).equals(channel.getCashOutStatus())
                : !Integer.valueOf(1).equals(channel.getPayStatus())) {
            throw new IllegalStateException("payment channel is disabled");
        }
        if (!"u2cPay".equals(channel.getBelongClass())) throw new IllegalArgumentException("channel capability is not implemented");
        ChannelStrategy strategy = strategies.get(channel.getBelongClass());
        if (strategy == null) {
            throw new IllegalArgumentException("unknown payment strategy: " + channel.getBelongClass());
        }
        return strategy;
    }

    public ChannelStrategy getStrategy(ChannelEnum channelEnum) {
        ChannelStrategy strategy = strategies.get(channelEnum.getValue());
        if (strategy == null) {
            throw new IllegalArgumentException("unknown payment strategy: " + channelEnum.getValue());
        }
        return strategy;
    }

    public Channel getChannelById(Integer channelId) {
        return channelService.getChannelById(channelId);
    }
}
