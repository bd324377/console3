package com.console.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.core.entity.UserProc;
import com.console.core.entity.Wallet;
import com.console.core.service.WalletService;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.constants.CacheConstants;
import com.console.framework.domain.BusinessException;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.request.RequestMember;
import com.console.framework.utils.*;
import com.console.user.entity.MarketSiteUser;
import com.console.user.entity.User;
import com.console.user.entity.UserRakeBackConfig;
import com.console.user.entity.UserShuntConfig;
import com.console.user.mapper.UserMapper;
import com.console.user.model.bo.InviteNewConfig;
import com.console.user.model.dto.req.UserReq;
import com.console.user.service.*;
import com.github.xiaolyuh.annotation.Cacheable;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Resource
    private MarketSiteUserService marketSiteUserService;
    @Resource
    private VipService vipService;
    @Resource
    private AgentService agentService;

    /**
     * 批量创建测试用户（当前租户通用）
     * @param createUserReq 创建信息
     * @return 测试用户
     */
    @Transactional(rollbackFor = Exception.class)
    public List<JSONObject> batchCreateUsers(UserReq.CreateUserReq createUserReq) {
        List<User> userList = generateUsers(createUserReq);
        List<MarketSiteUser> siteUserList = generateSiteUsers(userList);
        List<Wallet> walletList = generateWallets(userList,createUserReq.getUserAmt());
        if (this.saveBatch(userList) && walletService.saveBatch(walletList) && marketSiteUserService.saveBatch(siteUserList)) {
            List<JSONObject> resultList = new ArrayList<>();
            for (User user : userList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId",user.getId());
                jsonObject.put("account",user.getAccount());
                jsonObject.put("loginPwd",createUserReq.getLoginPwd());
                resultList.add(jsonObject);
            }
            return resultList;
        }
        throw new BusinessException(500,"创建测试用户失败，系统业务繁忙，请稍后再试");
    }

    @Override
    public boolean updateUser(String field, UserReq.UpdateUserReq updateUserReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getUser(RequestBackendUser.class);
        DynamicTableNameHandler.setTenantId(requestBackendUser.getTenantId());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,tenant_id,market_site_id,marketer_id", FieldUtils.convertToUnderscore(field)).lambda().eq(User::getId,updateUserReq.getId());
        User user = this.getOne(queryWrapper);
        updateUserReq.buildUpdateParam(field,user,requestBackendUser);
        if (StringUtils.hasText(updateUserReq.getErrorDesc())) {
            throw new BusinessException(500,updateUserReq.getErrorDesc());
        }
        if (this.update(updateUserReq.getUpdateWrapper())) {//更新成功
            adminProcMapper.insert(updateUserReq.getAdminProc());
            if (field.equals("remarks")) return true;
            RequestMember requestMember = TokenUtils.getRequestMember(user.getTenantId(),user.getMarketSiteId(), user.getMarketerId(), user.getId());
            if (requestMember != null) {
                if ("loginPwd".equals(field) || "status".equals(field)) {
                    TokenUtils.deleteUserToken(requestMember);
                } else {
                    switch (field) {
                        case "avatar"             -> requestMember.setAvatar(updateUserReq.getAvatar());
                        case "inviteRewardStatus" -> requestMember.setInviteRewardStatus(updateUserReq.getInviteRewardStatus());
                        case "phone"              -> requestMember.setPhone(user.getPhone());
                        case "email"              -> requestMember.setEmail(user.getEmail());
                        case "vipId"              -> requestMember.setVipId(updateUserReq.getVipId());
                        case "agentId"            -> requestMember.setAgentId(user.getAgentId());
                        case "cashOutStatus"      -> requestMember.setCashOutStatus(user.getCashOutStatus());
                        case "cashOutPwd"         -> requestMember.setCashOutPwd(user.getCashOutPwd());
                    }
                    TokenUtils.updateUserToken(requestMember);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public IPage<Object> getUserPage(Page<User> userPage, UserReq.SearchUserReq searchUserReq) {
        return null;
    }

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
            rakeBackConfigQueryWrapper.select("user_id,layers,rake_back_status,rake_back_range,rake_back_rate").lambda().eq(UserRakeBackConfig::getUserId,userId);
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

    /** 根据信息创建新用户信息 **/
    private List<User> generateUsers(UserReq.CreateUserReq createUserReq) {
        Integer tenantId = TenantUtils.getTenantId();
        Integer initialVipId = vipService.getInitialVipId();
        Integer initialAgentId = agentService.getInitialAgentId();
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < createUserReq.getAccountCount(); i++) {
            User user = new User();
            user.setUserType(User.UserType.TEST);
            user.setRegTime(LocalDateTime.now());
            user.setTenantId(tenantId);
            user.setVipId(initialVipId);
            user.setAgentId(initialAgentId);
            user.setAccount(createUserReq.generateAccount());
            user.setLoginPwd(EncryptUtils.md5Encrypt(createUserReq.getLoginPwd()));
            userList.add(user);
        }
        return userList;
    }

    /** 根据信息创建新用户信息 **/
    private List<MarketSiteUser> generateSiteUsers(List<User> userList) {
        List<MarketSiteUser> siteUserList = new ArrayList<>();
        for (User user : userList) {
            siteUserList.add(marketSiteUserService.initUserId(user));
        }
        return siteUserList;
    }

    /** 根据信息创建新用户钱包信息 **/
    private List<Wallet> generateWallets(List<User> userList, BigDecimal userAmt) {
        List<Wallet> walletList = new ArrayList<>();
        for (User user : userList) {
            Wallet userWallet = new Wallet();
            userWallet.setUserId(user.getId());
            userWallet.setBalance(userAmt);
            userWallet.setToBeOrderAmt(userAmt);
            userWallet.setOrderMode(2);
            userWallet.setWalletType(Wallet.WalletType.USER_WALLET);
            walletList.add(userWallet);

            Wallet agentWallet = new Wallet();
            agentWallet.setUserId(user.getId());
            agentWallet.setWalletType(Wallet.WalletType.AGENT_WALLET);
            walletList.add(agentWallet);
        }
        return walletList;
    }
}
