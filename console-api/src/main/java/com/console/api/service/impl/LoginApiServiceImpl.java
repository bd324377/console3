package com.console.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.console.api.dto.req.LoginReq;
import com.console.api.service.LoginApiService;
import com.console.core.entity.Parameter;
import com.console.core.entity.UserProc;
import com.console.core.model.bo.MarketSiteBo;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.constants.ParameterConstants;
import com.console.framework.domain.BusinessException;
import com.console.framework.domain.LoginResult;
import com.console.framework.domain.ResultCode;
import com.console.framework.request.RequestMember;
import com.console.framework.utils.EncryptUtils;
import com.console.framework.utils.I18nUtil;
import com.console.framework.utils.RequestUtils;
import com.console.framework.utils.TokenUtils;
import com.console.user.entity.User;
import com.console.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LoginApiServiceImpl extends BaseServiceImpl implements LoginApiService {
    @Resource
    private UserService userService;

    /**
     * 用户登录
     * @param loginReq 登录参数
     */
    @Override
    public LoginResult login(LoginReq loginReq) {
        MarketSiteBo marketSite = marketSiteService.getMarketSiteByDomain(RequestUtils.getRequestDomain());
        verifySiteValidity(marketSite);//校验租户，站点情况
        DynamicTableNameHandler.setTenantId(marketSite.getTenantId());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,account,tenant_id,market_site_id,market_team_id,marketer_id,avatar," +
                "email,email_status,phone,phone_status,agent_id,vip_id,status").lambda().eq(User::getAccount,loginReq.getAccount());
        User user = userService.getOne(queryWrapper);
        if (user == null) throw new BusinessException(500, I18nUtil.getI8nMsg("login.error.account.unRegister"));
        checkLoginUser(user, loginReq.getPassword(), marketSite.getMarketTeamId());
        RequestMember requestMember = user.buildRequestUser();
        requestMember.setLoginChannel(loginReq.getLoginChanel().getKey());
        TokenUtils.deleteUserToken(requestMember);
        UserProc userProc = new UserProc(user.getId(), user.getTenantId(), UserProc.ProcType.LONGIN);
        userProc.setLoginChannel(loginReq.getLoginChanel());
        userService.updateUserByLogin(userProc);
        return TokenUtils.createUserToken(requestMember);
    }

    /**
     * 刷新ac token
     * @param refreshToken 刷新token
     */
    @Override
    public String refreshUserToken(String refreshToken) {
        String userInfoStr = TokenUtils.getUserInfoStrByRefreshToken(refreshToken);
        if (!StringUtils.hasText(userInfoStr)) {
            throw new BusinessException(ResultCode.AUTHENTICATION_FAILED);
        }
        String[] userInfos = userInfoStr.split(";");
        try {
            Integer tenantId = Integer.valueOf(userInfos[0]);
            Integer userId = Integer.valueOf(userInfos[1]);
            DynamicTableNameHandler.setTenantId(tenantId);
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("id,account,tenant_id,market_site_id,market_team_id,marketer_id,avatar," +
                            "email,email_status,phone,phone_status,agent_id,vip_id").lambda().eq(User::getId,userId);
            User user = userService.getOne(queryWrapper);
            if (user != null) {
                RequestMember requestMember = user.buildRequestUser();
                return TokenUtils.refreshUserAccessToken(requestMember);
            } else {
                throw new BusinessException(ResultCode.AUTHENTICATION_FAILED);
            }
        } catch (Exception e) {
            throw new BusinessException(ResultCode.AUTHENTICATION_FAILED);
        } finally {
            DynamicTableNameHandler.removeTenantId();
        }
    }

    private void checkLoginUser(User user, String loginPed, Integer marketTeamId) {
        if (!user.getLoginPwd().equals(loginPed)) {
            Parameter supPwdParameter = parameterService.getParameterByCode(marketTeamId,ParameterConstants.P1029);
            if (supPwdParameter != null && supPwdParameter.getIsEnable() == 1) {
                String superPwd = EncryptUtils.md5Encrypt(supPwdParameter.getValue());
                if (!superPwd.equals(loginPed)) {
                    throw new BusinessException(500, I18nUtil.getI8nMsg("login.error.loginPwd.error"));
                }
            } else {
                throw new BusinessException(500, I18nUtil.getI8nMsg("login.error.loginPwd.error"));
            }
        }
        if (user.getStatus() != 1) {
            if (user.getStatus() == 2) {
                //冻结
                throw new BusinessException(500, I18nUtil.getI8nMsg("login.error.account.freeze"));
            } else {
                //删除
                throw new BusinessException(500, I18nUtil.getI8nMsg("login.error.account.delete"));
            }
        }
    }
}
