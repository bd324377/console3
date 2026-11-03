package com.console.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.core.entity.AdminProc;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.FieldUtils;
import com.console.framework.utils.RequestUtils;
import com.console.payment.entity.Channel;
import com.console.payment.model.ChannelReq;
import com.console.payment.model.dto.ChannelDTO;
import com.console.payment.service.ChannelService;
import com.console.payment.mapper.ChannelMapper;
import com.github.xiaolyuh.annotation.CacheEvict;
import com.github.xiaolyuh.annotation.Cacheable;
import com.github.xiaolyuh.annotation.Caching;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChannelServiceImpl extends BaseServiceImpl<ChannelMapper, Channel> implements ChannelService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = CacheConstants.PAYMENT_CHANNEL,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #channel.id"),
            @CacheEvict(value = CacheConstants.PAYMENT_CHANNEL,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #channel.merchantId"),
            @CacheEvict(value = CacheConstants.PAYMENT_CHANNEL_LIST,key = "T(com.console.framework.utils.TenantUtils).getTenantId()")
    })
    public boolean addChannel(Channel channel) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        if (this.save(channel)) {
            AdminProc adminProc = new AdminProc(requestBackendUser, AdminProc.ProcBelong.CHANNEL, AdminProc.ProcType.ADD, channel.getId(),null,channel);
            adminProcMapper.insert(adminProc);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = CacheConstants.PAYMENT_CHANNEL,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #delChannelReq.channelId"),
            @CacheEvict(value = CacheConstants.PAYMENT_CHANNEL,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #delChannelReq.merchantId"),
            @CacheEvict(value = CacheConstants.PAYMENT_CHANNEL_LIST,key = "T(com.console.framework.utils.TenantUtils).getTenantId()")
    })
    public boolean delChannel(ChannelReq.DelChannelReq delChannelReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        if (this.removeById(delChannelReq.getChannelId())) {
            AdminProc adminProc = new AdminProc(requestBackendUser, AdminProc.ProcBelong.CHANNEL, AdminProc.ProcType.DELETE, delChannelReq.getChannelId(),null,delChannelReq.getChannelId());
            adminProcMapper.insert(adminProc);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = CacheConstants.PAYMENT_CHANNEL,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #updateProductTypeReq.id"),
            @CacheEvict(value = CacheConstants.PAYMENT_CHANNEL,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #updateProductTypeReq.merchantId"),
            @CacheEvict(value = CacheConstants.PAYMENT_CHANNEL_LIST,key = "T(com.console.framework.utils.TenantUtils).getTenantId()")
    })
    public boolean updateChannel(String field, ChannelReq.UpdateChannelReq updateChannelReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        QueryWrapper<Channel> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", FieldUtils.convertToUnderscore(field)).lambda().eq(Channel::getId,updateChannelReq.getId());
        Channel channel = this.getOne(queryWrapper);
        updateChannelReq.buildUpdateParam(field,channel,requestBackendUser);
        if (this.update(updateChannelReq.getUpdateWrapper())) {
            adminProcMapper.insert(updateChannelReq.getAdminProc());
            return true;
        }
        return false;
    }

    @Override
    public IPage<Channel> getChannelPage(Page<Channel> page, ChannelReq.SearchChannelReq searchChannelReq) {
        QueryWrapper<Channel> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(searchChannelReq.getMerchantId() != null,Channel::getMerchantId,searchChannelReq.getMerchantId())
                .likeRight(StringUtils.hasText(searchChannelReq.getChannelName()),Channel::getChannelName,searchChannelReq.getChannelName())
                .likeRight(StringUtils.hasText(searchChannelReq.getShowName()),Channel::getShowName,searchChannelReq.getShowName());
        return this.page(page,queryWrapper);
    }

    @Override
    @Cacheable(value = CacheConstants.PAYMENT_CHANNEL,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #channelId")
    public Channel getChannelById(Integer channelId) {
        return this.getById(channelId);
    }
    @Override
    @Cacheable(value = CacheConstants.PAYMENT_CHANNEL_MERCHANT_ID,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #merchantId")
    public Channel getChannelByMerchantId(String merchantId) {
        QueryWrapper<Channel> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Channel::getMerchantId,merchantId);
        return this.getOne(queryWrapper);
    }

    @Override
    @Cacheable(value = CacheConstants.PAYMENT_CHANNEL_LIST,key = "T(com.console.framework.utils.TenantUtils).getTenantId()")
    public List<ChannelDTO> getChannelList() {
        QueryWrapper<Channel> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,icon,show_name,pay_status,real_name_pay_status,cash_out_status," +
                        "min_pay_amt,max_pay_amt,min_cash_out_amt,max_cash_out_amt,pay_amt_arr")
                .lambda().eq(Channel::getPayStatus,1).or().eq(Channel::getCashOutStatus,1);
        List<Channel> channelList = this.list(queryWrapper);
        if (ObjectUtils.isEmpty(channelList)) return null;
        List<ChannelDTO> channelDTOS = new ArrayList<>();
        for (Channel channel : channelList) {
            ChannelDTO channelDTO = new ChannelDTO();
            BeanUtils.copyProperties(channel,channelDTO);
            channelDTOS.add(channelDTO);
        }
        return channelDTOS;
    }
}
