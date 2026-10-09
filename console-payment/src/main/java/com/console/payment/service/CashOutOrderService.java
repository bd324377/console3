package com.console.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.entity.CashOutOrder;

public interface CashOutOrderService extends IService<CashOutOrder> {
    boolean handleCashOutOrder(CashOutOrder order, ChannelResult result);
}
