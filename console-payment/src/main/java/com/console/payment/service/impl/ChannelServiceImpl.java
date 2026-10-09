package com.console.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.payment.entity.Channel;
import com.console.payment.service.ChannelService;
import com.console.payment.mapper.ChannelMapper;
import com.github.xiaolyuh.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ChannelServiceImpl extends ServiceImpl<ChannelMapper, Channel> implements ChannelService {

    @Override
    @Cacheable(value = CacheConstants.PAYMENT_CHANNEL,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #channelId")
    public Channel getChannelById(Integer channelId) {
        return this.getById(channelId);
    }
    @Override
    @Cacheable(value = CacheConstants.PAYMENT_CHANNEL_MERCHANT_ID,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #siteId + ':' + #merchantId")
    public Channel getChannelByMerchantId(Integer siteId, String merchantId) {
        QueryWrapper<Channel> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Channel::getAppKey,merchantId).eq(Channel::getMarketSiteId,siteId);
        return this.getOne(queryWrapper);
    }

}
