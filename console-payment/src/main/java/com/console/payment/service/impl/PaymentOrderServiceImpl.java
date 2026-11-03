package com.console.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.console.core.entity.Wallet;
import com.console.framework.domain.BusinessException;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.RequestUtils;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.entity.*;
import com.console.payment.mapper.*;
import com.console.payment.model.PaymentOrderReq;
import com.console.payment.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static com.console.payment.entity.PaymentOrder.PaymentStatus.*;

@Service
@RequiredArgsConstructor
public class PaymentOrderServiceImpl extends ServiceImpl<PaymentOrderMapper, PaymentOrder> implements PaymentOrderService {

    @Override
    public boolean createPaymentOrder(PaymentOrderReq.CreatePaymentOrderReq createPaymentOrderReq) {
        if(createPaymentOrderReq.getOrderAmt().compareTo(BigDecimal.ZERO) < 0 || createPaymentOrderReq.getToBeOrderAmt().compareTo(BigDecimal.ZERO) < 0){
            throw new BusinessException(500, "人工入款失败，输入的金额不能小于0");
        }
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        return false;
    }

    @Override
    public IPage<PaymentOrder> getPaymentOrderPage(Page<PaymentOrder> page, PaymentOrderReq.SearchPaymentOrderReq searchPaymentOrderReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        if (searchPaymentOrderReq.getMarketSiteId() == null) {
            if (requestBackendUser.getAccountType() == 2) {//管理员用户

            }
        }
        return this.page(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handlePaymentOrder(PaymentOrder supplied, ChannelResult result) {
        return false;
    }


}
