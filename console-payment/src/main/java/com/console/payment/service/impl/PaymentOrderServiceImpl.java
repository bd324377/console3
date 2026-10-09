package com.console.payment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.console.core.entity.Wallet;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.entity.*;
import com.console.payment.mapper.*;
import com.console.payment.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static com.console.payment.entity.PaymentOrder.PaymentStatus.*;

@Service
@RequiredArgsConstructor
public class PaymentOrderServiceImpl extends ServiceImpl<PaymentOrderMapper, PaymentOrder> implements PaymentOrderService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handlePaymentOrder(PaymentOrder supplied, ChannelResult result) {
        return false;
    }


}
