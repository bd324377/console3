package com.console.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.entity.CashOutOrder;

public interface CashOutOrderService extends IService<CashOutOrder> {

    /**
     * 处理已通过验签和一致性校验的提现回调。
     *
     * @return 本次回调是否实际更新了订单
     */
    boolean handleCashOutOrder(CashOutOrder order, ChannelResult result);
}
