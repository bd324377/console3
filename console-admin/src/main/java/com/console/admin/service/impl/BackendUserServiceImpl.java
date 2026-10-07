package com.console.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.admin.dto.req.BackendUserReq;
import com.console.admin.entity.BackendUser;
import com.console.core.entity.Tenant;
import com.console.admin.mapper.BackendUserMapper;
import com.console.admin.service.BackendUserService;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.domain.BusinessException;
import com.console.framework.domain.LoginResult;
import com.console.framework.domain.ResultCode;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class BackendUserServiceImpl extends BaseServiceImpl<BackendUserMapper, BackendUser> implements BackendUserService {

    @Override
    public boolean createAdminUser(BackendUserReq.CreateBackendUserReq createBackendUserReq) {
        BackendUser backendUser = new BackendUser();
        BeanUtils.copyProperties(createBackendUserReq,backendUser);
        backendUser.setAccountType(BackendUser.AccountType.UNENABLE);
        backendUser.setLoginPwd(EncryptUtils.md5Encrypt("666666"));
        return false;
    }

    @Override
    public boolean createMarketer(BackendUserReq.CreateBackendUserReq createBackendUserReq) {
        return false;
    }

    @Override
    public IPage<BackendUser> getAdminPage(BackendUserReq.SearchBackendUserReq searchBackendUserReq) {
        QueryWrapper<BackendUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BackendUser::getAccountType,0)
                .eq(searchBackendUserReq.getRoleId() != null,BackendUser::getRole,searchBackendUserReq.getRoleId())
                .eq(StringUtils.hasText(searchBackendUserReq.getAccount()),BackendUser::getAccount,searchBackendUserReq.getAccount());
        Page<BackendUser> page = new Page<>(searchBackendUserReq.getCurrent(),searchBackendUserReq.getSize());
        return this.page(page,queryWrapper);
    }

    @Override
    public Map<String, Object> getMarketerPage(BackendUserReq.SearchBackendUserReq searchBackendUserReq) {
        return null;
    }

    @Override
    public LoginResult login(BackendUserReq.LoginReq loginReq) {
        Tenant tenant = tenantService.getTenantByBackendDomain(RequestUtils.getRequestDomain());//获取当前域名绑定的租户
        Tenant targetTenant = tenantService.getTenantById(loginReq.getTenantId());//获取需要登录的租户信息
        Tenant.verifyValidityOfAdmin(tenant,targetTenant);//租户校验
        CaptchaUtils.validateCaptcha(loginReq.getCaptcha(), loginReq.getUuid(), targetTenant.getId());
        DynamicTableNameHandler.setTenantId(tenant.getId());
        QueryWrapper<BackendUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,account,account_type,user_name,role,status,login_pwd,accessible_tenants,accessible_ips").lambda().eq(BackendUser::getAccount,loginReq.getAccount());
        BackendUser backendUser = this.getOne(queryWrapper);
        if (backendUser == null) {
            throw new BusinessException(500,I18nUtil.getI8nMsg("backendUser.error.login.account.notExist"));
        }
        validateLoginCredentials(backendUser, targetTenant.getId(), loginReq.getLoginPwd());
        RequestBackendUser requestBackendUser = backendUser.buildRequestUser(tenant.getId(), targetTenant.getId());
        TokenUtils.deleteBackendUserToken(requestBackendUser);
        return TokenUtils.createBackendUserToken(requestBackendUser);
    }

    @Override
    public String refreshBackendUserToken(String refreshToken) {
        String userInfoStr = TokenUtils.getUserInfoStrByRefreshToken(refreshToken);
        if (!StringUtils.hasText(userInfoStr)) {
            throw new BusinessException(ResultCode.AUTHENTICATION_FAILED);
        }
        String[] userInfos = userInfoStr.split(";");
        try {
            Integer targetTenantId = Integer.valueOf(userInfos[0]);
            Integer belongTenantId = Integer.valueOf(userInfos[1]);
            Integer userId = Integer.valueOf(userInfos[2]);
            DynamicTableNameHandler.setTenantId(belongTenantId);
            QueryWrapper<BackendUser> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("id,account,account_type,user_name,role,status,login_pwd,accessible_tenants,accessible_ips")
                    .lambda().eq(BackendUser::getId,userId);
            BackendUser backendUser = this.getOne(queryWrapper);
            if (backendUser != null) {
                RequestBackendUser requestBackendUser = backendUser.buildRequestUser(belongTenantId, targetTenantId);
                return TokenUtils.refreshBackendUserAccessToken(requestBackendUser);
            } else {
                throw new BusinessException(ResultCode.AUTHENTICATION_FAILED);
            }
        } catch (Exception e) {
            throw new BusinessException(ResultCode.AUTHENTICATION_FAILED);
        } finally {
            DynamicTableNameHandler.removeTenantId();
        }
    }

    /**
     * 登录校验 租户、IP和密码校验
     */
    private void validateLoginCredentials(BackendUser backendUser, Integer tenantId, String loginPwd) {
        if (backendUser == null) {
            throw new BusinessException(500, I18nUtil.getI8nMsg("backendUser.error.login.admin.adminNull"));
        }
        if (!backendUser.getLoginPwd().equals(loginPwd)) {
            throw new BusinessException(500, I18nUtil.getI8nMsg("backendUser.error.login.loginPwd.error"));
        }
        if (!ObjectUtils.isEmpty(backendUser.getAccessibleTenants()) && !backendUser.getAccessibleTenants().contains(tenantId)) {//没有登录该租户权限
            throw new BusinessException(500, I18nUtil.getI8nMsg("backendUser.error.login.user.limitTenant"));
        }
        if (!ObjectUtils.isEmpty(backendUser.getAccessibleIps()) && !backendUser.getAccessibleIps().contains(RequestUtils.getRequestIp())) {//没有使用该IP登录权限
            throw new BusinessException(500, I18nUtil.getI8nMsg("backendUser.error.login.user.limitIp"));
        }
    }
}
