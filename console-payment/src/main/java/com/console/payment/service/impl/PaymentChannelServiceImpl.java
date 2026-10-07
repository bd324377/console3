package com.console.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.payment.entity.PaymentChannel;
import com.console.payment.mapper.PaymentChannelMapper;
import com.console.payment.service.PaymentChannelService;
import com.github.xiaolyuh.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PaymentChannelServiceImpl extends ServiceImpl<PaymentChannelMapper, PaymentChannel> implements PaymentChannelService {
    @Override
    @Cacheable(value = CacheConstants.PAYMENT_CHANNEL_MERCHANT_ID,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #siteId + ':' + #merchantId")
    public PaymentChannel getChannelByMerchantId(Integer siteId,String merchantId) {
        QueryWrapper<PaymentChannel> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PaymentChannel::getAppKey,merchantId).eq(PaymentChannel::getMarketSiteId,siteId);
        return this.getOne(queryWrapper);
    }
}
