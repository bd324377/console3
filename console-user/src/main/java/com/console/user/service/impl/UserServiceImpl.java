package com.console.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.console.core.entity.UserProc;
import com.console.core.entity.Wallet;
import com.console.core.service.WalletService;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.constants.CacheConstants;
import com.console.user.entity.AgentLayer;
import com.console.user.entity.User;
import com.console.user.entity.UserRakeBackConfig;
import com.console.user.entity.UserShuntConfig;
import com.console.user.mapper.UserMapper;
import com.console.user.model.bo.InviteNewConfig;
import com.console.user.service.*;
import com.github.xiaolyuh.annotation.Cacheable;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class UserServiceImpl extends BaseServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private WalletService walletService;
    @Resource
    private UserProcService userProcService;
    @Resource
    private UserShuntConfigService userShuntConfigService;
    @Resource
    private UserRakeBackConfigService userRakeBackConfigService;
    @Resource
    private AgentLayerService agentLayerService;

    @Override
    @Cacheable( value = CacheConstants.USER_INVITE_NEW_CONFIG, key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #userId")
    public InviteNewConfig getUserInviteNewConfig(Integer userId) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.select("id,tenant_id,market_siteI_id,market_team_id,marketer_id,status").lambda().eq(User::getId,userId);
        User user = this.getOne(userQueryWrapper);
        if (user != null) {
            QueryWrapper<Wallet> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("order_mode,sub_user_order_mode").lambda().eq(Wallet::getId, userId);
            Wallet wallet = walletService.getOne(queryWrapper);
            QueryWrapper<UserShuntConfig> shuntConfigQueryWrapper = new QueryWrapper<>();
            shuntConfigQueryWrapper.lambda().eq(UserShuntConfig::getUserId, userId);
            List<UserShuntConfig> userShuntConfigs = userShuntConfigService.list(shuntConfigQueryWrapper);

            QueryWrapper<UserRakeBackConfig> rakeBackConfigQueryWrapper = new QueryWrapper<>();
            rakeBackConfigQueryWrapper.select("").lambda().eq(UserRakeBackConfig::getUserId,userId);
            List<UserRakeBackConfig> userRakeBackConfigs = userRakeBackConfigService.list(rakeBackConfigQueryWrapper);
            return new InviteNewConfig(user, wallet, userShuntConfigs, userRakeBackConfigs);
        }
        return null;
    }

    /**
     * 同步新用户相关配置（异步处理）
     * @param user              用户信息
     * @param inviteNewConfig   配置信息
     */
    @Async
    @Override
    public void syncNewUserConfig(User user,InviteNewConfig inviteNewConfig) {
        DynamicTableNameHandler.setTenantId(user.getTenantId());
        //同步分流和返佣配置
        List<UserShuntConfig> userShuntConfigList = inviteNewConfig.buildSubUserShuntConfig(user.getId());
        if (!ObjectUtils.isEmpty(userShuntConfigList)) {
            userShuntConfigService.saveBatch(userShuntConfigList);
        }
        List<UserRakeBackConfig> userRakeBackConfigList = inviteNewConfig.buildSubUserRakeBackConfig(user.getId());
        if (!ObjectUtils.isEmpty(userRakeBackConfigList)) {
            userRakeBackConfigService.saveBatch(userRakeBackConfigList);
        }
        //添加r_agent_layer层级信息
        agentLayerService.addAgentLayerBatch(user,inviteNewConfig);
        //同步总租户
        DynamicTableNameHandler.setTenantId(0);
        this.save(user);
    }

    /**
     * 登录后更新用户信息
     * @param userProc 用户登录过程记录
     */
    @Async
    @Override
    public void updateUserByLogin(UserProc userProc) {
        try {
            DynamicTableNameHandler.setTenantId(userProc.getTenantId());
            userProcService.save(userProc);
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda()
                    .set(User::getLoginIp, userProc.getIp())
                    .set(User::getLoginTime, userProc.getProcTime())
                    .set(User::getLoginChannel, userProc.getLoginChannel())
                    .eq(User::getId, userProc.getUserId());
            this.update(updateWrapper);
        } finally {
            DynamicTableNameHandler.removeTenantId();
        }
    }
}
