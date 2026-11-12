package com.console.api.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.api.dto.req.UserReq;
import com.console.api.dto.res.SubTeamRechargeReport;
import com.console.api.dto.res.SubUserPageRes;
import com.console.api.mapper.AgentAPIMapper;
import com.console.api.service.AgentAPIService;
import com.console.core.entity.Parameter;
import com.console.framework.constants.ParameterConstants;
import com.console.framework.request.RequestMember;
import com.console.framework.utils.RequestUtils;
import com.console.user.entity.User;
import com.console.user.model.bo.VipBo;
import com.console.user.service.VipService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AgentAPIServiceImpl extends BaseServiceImpl implements AgentAPIService {
    @Resource
    private AgentAPIMapper agentAPIMapper;
    @Resource
    private VipService vipService;
    @Override
    public SubUserPageRes getSubUserPage(UserReq.SearchSubUserReq searchSubUserReq) {
        RequestMember requestMember = RequestUtils.getRequestMember();
        Parameter parameter = parameterService.getParameterByCode(requestMember.getTeamId(), ParameterConstants.P1003);
        if (parameter == null) {
            searchSubUserReq.setLayers(3);
        } else {
            searchSubUserReq.setLayers(Integer.valueOf(parameter.getValue()));
        }

        Page<User> page = new Page<>(searchSubUserReq.getCurrent(),searchSubUserReq.getSize());
        if (searchSubUserReq.getUserId() == null) {
            searchSubUserReq.setUserId(requestMember.getId());
        }
        IPage<User> subUserPage = agentAPIMapper.selectSubUserPage(page,searchSubUserReq);
        if (!ObjectUtils.isEmpty(subUserPage.getRecords())) {
            List<Integer> vipIds = subUserPage.getRecords().stream().map(User::getVipId).toList();
            Map<Integer, Integer> vipLevelMap = new HashMap<>();
            for (Integer vipId : vipIds) {
                VipBo vip = vipService.getVipById(vipId);
                vipLevelMap.put(vipId, vip.getLevel());
            }
            for (User user : subUserPage.getRecords()) {
                user.setVipLevel(vipLevelMap.get(user.getVipId()));
                user.setVipId(null);
            }
        }
        SubUserPageRes subUserPageRes = new SubUserPageRes(subUserPage);

        if (searchSubUserReq.getCurrent() == 1) {
            SubTeamRechargeReport subTeamRechargeReport = agentAPIMapper.selectSubTeamRechargeReport(searchSubUserReq);
            if (subTeamRechargeReport != null) {
                subUserPageRes.setSubTeamRechargeReport(subTeamRechargeReport);
            }
        }
        return subUserPageRes;
    }
}
