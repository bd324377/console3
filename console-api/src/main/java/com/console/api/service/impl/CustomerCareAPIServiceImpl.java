package com.console.api.service.impl;

import com.console.admin.entity.CustomerCare;
import com.console.admin.service.CustomerCareService;
import com.console.api.service.CustomerCareAPIService;
import com.console.core.model.bo.MarketerBo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerCareAPIServiceImpl extends BaseServiceImpl implements CustomerCareAPIService {
    @Resource
    private CustomerCareService customerCareService;

    @Override
    public List<CustomerCare> getCustomerCares() {
        MarketerBo marketerBo = getMarketerBo();
        return customerCareService.getCustomerCareList(marketerBo.getTenantId(),
                marketerBo.getMarketSiteId(), marketerBo.getMarketTeamId(), marketerBo.getMarketerId());
//        RequestMember requestMember = RequestUtils.getRequestMember();
//        if (requestMember != null) {
//            DynamicTableNameHandler.setTenantId(requestMember.getTenantId());
//            return customerCareService.getCustomerCareList(requestMember.getTenantId(),
//                    requestMember.getSiteId(), requestMember.getTeamId(), requestMember.getMarketerId());
//        } else {
//            MarketSiteBo marketSite = marketSiteService.getMarketSiteByDomain(RequestUtils.getRequestDomain());
//            DynamicTableNameHandler.setTenantId(marketSite.getTenantId());
//            int inviteType = 2;Integer inviteCode = null;    //邀请类型 默认渠道推广 1、代理；2、站点业务员
//            String inviteCodeStr = RequestUtils.getHttpHeader("inviteCode");
//            if (StringUtils.hasText(inviteCodeStr)) {//有带邀请码
//                String inviteCodeData = EncryptUtils.decodeFromBase64Str(inviteCodeStr);
//                String[] inviteCodeDatas = inviteCodeData.split("_");
//                inviteType = inviteCodeDatas.length >= 2 ? Integer.parseInt(inviteCodeDatas[0]) : inviteType;
//                inviteCode = inviteCodeDatas.length >= 2 ? Integer.valueOf(inviteCodeDatas[1]) : inviteCode;
//
//                if (inviteCode != null) {
//                    MarketerBo marketer;
//                    if (inviteType == 2) {//业务员邀新
//                        marketer = marketerService.getMarketer(inviteCode);
//                    } else {//代理邀新
//                        InviteNewConfig inviteNewConfig = userService.getUserInviteNewConfig(inviteCode);
//                        marketer = marketerService.getMarketer(inviteNewConfig.getMarketerId());
//                    }
//                    if (marketer != null && marketer.getSiteStatus() != MarketSite.SiteStatus.CLOSED) {
//                        return customerCareService.getCustomerCareList(marketer.getTenantId(),
//                                marketer.getSiteId(), marketer.getMarketTeamId(), marketer.getMarketerId());
//                    }
//                }
//            }
//            return customerCareService.getCustomerCareList(marketSite.getTenantId(),
//                    marketSite.getMarketSiteId(), marketSite.getMarketTeamId(), marketSite.getMarketerId());
//        }
    }
}
