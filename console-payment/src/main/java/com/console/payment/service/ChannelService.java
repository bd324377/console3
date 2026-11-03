package com.console.payment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.console.payment.entity.Channel;
import com.console.payment.model.ChannelReq;
import com.console.payment.model.dto.ChannelDTO;

import java.util.List;

public interface ChannelService extends IService<Channel> {
    boolean addChannel(Channel channel);
    boolean delChannel(ChannelReq.DelChannelReq delChannelReq);
    boolean updateChannel(String field, ChannelReq.UpdateChannelReq updateChannelReq);
    IPage<Channel> getChannelPage(Page<Channel> page, ChannelReq.SearchChannelReq searchChannelReq);
    Channel getChannelById(Integer channelId);
    Channel getChannelByMerchantId(String merchantId);
    List<ChannelDTO> getChannelList();
}
