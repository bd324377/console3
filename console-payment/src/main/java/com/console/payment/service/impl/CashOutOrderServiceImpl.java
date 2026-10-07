package com.console.payment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.console.payment.entity.CashOutOrder;
import com.console.payment.mapper.CashOutOrderMapper;
import com.console.payment.service.CashOutOrderService;
import org.springframework.stereotype.Service;

@Service
public class CashOutOrderServiceImpl extends ServiceImpl<CashOutOrderMapper, CashOutOrder> implements CashOutOrderService {
}
