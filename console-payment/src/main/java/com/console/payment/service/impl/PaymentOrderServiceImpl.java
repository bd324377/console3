package com.console.payment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.console.payment.entity.PaymentOrder;
import com.console.payment.mapper.PaymentOrderMapper;
import com.console.payment.service.PaymentOrderService;
import org.springframework.stereotype.Service;

@Service
public class PaymentOrderServiceImpl extends ServiceImpl<PaymentOrderMapper, PaymentOrder> implements PaymentOrderService {
}
