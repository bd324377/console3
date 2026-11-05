package com.console.api.service.impl;

import com.console.api.dto.res.SelfInfoRes;
import com.console.api.service.UserAPIService;
import com.console.framework.domain.BusinessException;
import com.console.framework.domain.ResultCode;
import com.console.framework.request.RequestMember;
import com.console.framework.utils.RequestUtils;
import com.console.user.entity.Vip;
import com.console.user.model.bo.VipBo;
import com.console.user.service.VipService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class UserAPIServiceImpl implements UserAPIService {
    @Resource
    private VipService vipService;

    @Override
    public SelfInfoRes selfInfo() {
        RequestMember requestMember = RequestUtils.getRequestMember();
        if (requestMember == null) {
            if (StringUtils.hasText(RequestUtils.getTokenKey())) {
                log.info("查询用户信息未失败，当前用户未登录，当前请求的token信息为：{}", RequestUtils.getTokenKey());
            }
            throw new BusinessException(ResultCode.AUTHENTICATION_FAILED);
        }
        SelfInfoRes selfInfoRes = new SelfInfoRes();
        selfInfoRes.setAccount(requestMember.getAccount());
        selfInfoRes.setUserId(requestMember.getId());
        selfInfoRes.setAvatar(requestMember.getAvatar());

        VipBo vip = vipService.getVipById(requestMember.getVipId());
        selfInfoRes.setVipName(vip.getVipName());
        selfInfoRes.setVipIcon(vip.getIcon());
        selfInfoRes.setUpgradeRechargeAmt(vip.getUpgradeRechargeAmt());
        selfInfoRes.setUpgradeOrderAmt(vip.getUpgradeOrderAmt());
        selfInfoRes.setOrderAmt(requestMember.getOrderAmt());
        selfInfoRes.setRechargeAmt(requestMember.getRechargeAmt());

        selfInfoRes.setPhone(requestMember.getPhone());
        selfInfoRes.setPhoneStatus(requestMember.getPhoneStatus());

        selfInfoRes.setHasCashOutPwd(StringUtils.hasText(requestMember.getCashOutPwd()));

        return selfInfoRes;
    }
}
