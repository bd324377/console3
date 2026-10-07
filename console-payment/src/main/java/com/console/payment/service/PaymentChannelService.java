package com.console.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.payment.entity.PaymentChannel;

public interface PaymentChannelService extends IService<PaymentChannel> {
    PaymentChannel getChannelByMerchantId(Integer siteId,String merchantId);
}
