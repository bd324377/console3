package com.console.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.payment.entity.Channel;

public interface ChannelService extends IService<Channel> {
    Channel getChannelById(Integer channelId);
    Channel getChannelByMerchantId(Integer siteId, String merchantId);
}
