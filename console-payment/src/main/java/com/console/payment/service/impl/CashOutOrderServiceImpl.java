package com.console.payment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.entity.CashOutOrder;
import com.console.payment.mapper.CashOutOrderMapper;
import com.console.payment.service.CashOutOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CashOutOrderServiceImpl extends ServiceImpl<CashOutOrderMapper, CashOutOrder> implements CashOutOrderService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleCashOutOrder(CashOutOrder supplied, ChannelResult result) {
        return true;
    }
}
