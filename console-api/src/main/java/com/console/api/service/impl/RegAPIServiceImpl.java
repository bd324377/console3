package com.console.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.console.api.dto.req.RegReq;
import com.console.api.service.RegAPIService;
import com.console.core.entity.Parameter;
import com.console.core.entity.UserProc;
import com.console.core.entity.Wallet;
import com.console.core.model.bo.MarketSiteBo;
import com.console.core.model.bo.MarketerBo;
import com.console.core.service.*;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.constants.CacheConstants;
import com.console.framework.constants.ParameterConstants;
import com.console.framework.domain.BusinessException;
import com.console.framework.request.RequestMember;
import com.console.framework.utils.*;
import com.console.user.entity.MarketSiteUser;
import com.console.user.entity.User;
import com.console.user.entity.UserShuntConfig;
import com.console.user.model.bo.InviteNewConfig;
import com.console.user.service.*;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.List;

@Service
public class RegAPIServiceImpl implements RegAPIService {
    @Resource
    private MarketSiteService marketSiteService;
    @Resource
    private MarketerService marketerService;
    @Resource
    private MarketSiteUserService marketSiteUserService;
    @Resource
    private UserService userService;
    @Resource
    private UserProcService userProcService;
    @Resource
    private WalletService walletService;
    @Resource
    private VipService vipService;
    @Resource
    private AgentService agentService;
    @Resource
    private ParameterService parameterService;
    private final SecureRandom invitationConversionSecureRandom = new SecureRandom();// 使用密码学安全的随机数生成器，计算邀新有效转化率
    private static final Object invitationConversionLock = new Object();// 双重锁定机制确保线程安全，避免多个线程同时创建用户
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object register(RegReq regReq) {
        //判断租户和站点情况
        MarketSiteBo marketSite = marketSiteService.getMarketSiteByDomain(RequestUtils.getRequestDomain());
        DynamicTableNameHandler.setTenantId(marketSite.getTenantId());
        verifyValidityMarketSite(marketSite);
        DynamicTableNameHandler.setTenantId(marketSite.getTenantId());
        //判断邀请人情况
        int inviteType = 2;Integer inviteCode = null;    //邀请类型 默认渠道推广 1、代理；2、站点业务员
        if (StringUtils.hasText(regReq.getInviteCode())) {
            String inviteCodeData = EncryptUtils.decodeFromBase64Str(regReq.getInviteCode());
            String[] inviteCodeDatas = inviteCodeData.split("_");
            inviteType = inviteCodeDatas.length >= 2 ? Integer.parseInt(inviteCodeDatas[0]) : inviteType;
            inviteCode = inviteCodeDatas.length >= 2 ? Integer.valueOf(inviteCodeDatas[1]) : inviteCode;
        }
        InviteNewConfig inviteNewConfig;
        if (inviteType == 2) {//业务员邀新
            MarketerBo marketer = marketerService.getMarketer(inviteCode);
            MarketerBo.verifyValidity(marketer);
            inviteNewConfig = new InviteNewConfig(marketer);
        } else {//代理邀新
            inviteNewConfig = userService.getUserInviteNewConfig(inviteCode);
            MarketerBo marketer = marketerService.getMarketer(inviteNewConfig.getMarketerId());
            MarketerBo.verifyValidity(marketer);
            boolean shuntStatus = buildShuntStatus(inviteNewConfig);
            inviteNewConfig.setShuntStatus(shuntStatus);
            //判断是否继承上级用户的分流规则
        }
        checkRegParam(inviteNewConfig, regReq.getRegDeviceId());//
        User user = regReq.initUser();
        inviteNewConfig.buildUserByConfig(user);
        MarketSiteUser marketSiteUser = marketSiteUserService.initUserId(user);
        user.setVipId(vipService.getInitialVipId());
        user.setAgentId(agentService.getInitialAgentId());
        Wallet userWallet = new Wallet();
        userWallet.setUserId(user.getId());
        userWallet.setWalletType(Wallet.WalletType.USER_WALLET);
        userWallet.setOrderMode(inviteNewConfig.getSubUserOrderMode());
        userWallet.setSubUserOrderMode(inviteNewConfig.getSubUserOrderMode());
        Wallet agentWallet = new Wallet();
        agentWallet.setUserId(user.getId());
        agentWallet.setWalletType(Wallet.WalletType.AGENT_WALLET);
        UserProc regProc = new UserProc(user.getId(), marketSite.getTenantId(), UserProc.ProcType.REGISTER);
        regProc.setProcValue(new BigDecimal(regReq.getRegChannel().getKey()));
        try {
            if (userService.save(user) && walletService.saveBatch(List.of(userWallet,agentWallet)) &&
                    userProcService.save(regProc) && marketSiteUserService.save(marketSiteUser)) {//异步处理后续事项
                userService.syncNewUserConfig(user,inviteNewConfig);
            }
        } catch (DuplicateKeyException e) {
            String uniqueKey = UniqueKeyUtils.extractUniqueKey(e);
            if ("uk_user_account".equals(uniqueKey)) {
                throw new BusinessException(500,I18nUtil.getI8nMsg("reg.error.account.alreadyExists"));
            }
            if ("uk_user_phone".equals(uniqueKey)) {
                throw new BusinessException(500,I18nUtil.getI8nMsg("reg.error.phone.alreadyExists"));
            }
            if ("uk_user_email".equals(uniqueKey)) {
                throw new BusinessException(500,I18nUtil.getI8nMsg("reg.error.email.alreadyExists"));
            }
            throw new BusinessException(500,I18nUtil.getI8nMsg("reg.error.user.alreadyExists"));
        }
        RequestMember requestMember = user.buildRequestUser();
        TokenUtils.deleteUserToken(requestMember);
        return TokenUtils.createUserToken(requestMember);
    }

    /**
     * 分流校验
     * @param inviteNewConfig 用户邀新配置
     * @return 是否分流
     */
    private boolean buildShuntStatus(InviteNewConfig inviteNewConfig) {
        if (inviteNewConfig.getUserShuntConfigs() != null) {
            UserShuntConfig shuntConfig = inviteNewConfig.getUserShuntConfigs().get(1);
            if (shuntConfig == null || shuntConfig.getShuntStatus() == 0 || shuntConfig.getShuntRate().compareTo(BigDecimal.ZERO) <= 0) return false;//没有设定分流规则或已关闭
            if (shuntConfig.getShuntThreshold() != null && shuntConfig.getShuntThreshold() > 0) {//存在分流阈值，则需要判断当前邀请人是否邀新达到阈值
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(User::getParentId, inviteNewConfig.getUserId());
                long userCount = userService.count(queryWrapper);
                if (userCount < shuntConfig.getShuntThreshold()) return false;
            }
            //判断概率
            if (shuntConfig.getShuntRate().compareTo(BigDecimal.valueOf(100)) >= 0) return true;
            double shuntRate = shuntConfig.getShuntRate().divide(BigDecimal.valueOf(100),2, RoundingMode.HALF_UP).doubleValue();
            synchronized (invitationConversionLock) {
                return invitationConversionSecureRandom.nextDouble() < shuntRate;
            }
        }
        return false;
    }

    /**
     * 校验注册参数
     * @param inviteNewConfig 邀新配置信息
     * @param regDeviceId 注册设备ID
     */
    private void checkRegParam(InviteNewConfig inviteNewConfig, String regDeviceId) {
        //判断同IP，同设备，代理IP是否分流
        Parameter sameIpLimitParameter = parameterService.getParameterByCode(inviteNewConfig.getMarketTeamId(), ParameterConstants.P1018);
        if (sameIpLimitParameter != null && sameIpLimitParameter.getIsEnable() == 1) {
            long sameIpUserCount = queryUserCount(1, inviteNewConfig.getMarketSiteId(), RequestUtils.getRequestIp());//同IP注册人数
            Parameter.RegLimitParam ipRegLimitParam = JSON.parseObject(sameIpLimitParameter.getValue(), Parameter.RegLimitParam.class);
            if (ipRegLimitParam.getRegLimitStatus() == 1 && sameIpUserCount > ipRegLimitParam.getRegThreshold()) {//开启注册限制
                throw new BusinessException(500,I18nUtil.getI8nMsg("reg.error.IP.outLimit"));
            }
            if (ipRegLimitParam.getShuntStatus() == 1 && sameIpUserCount > ipRegLimitParam.getShuntThreshold()) {//开启分流处理
                inviteNewConfig.setShuntStatus(true);
                inviteNewConfig.setShuntReason("reg.shunt.reason.ipOutLimit");
            }
            if (ipRegLimitParam.getModifyOrderModeStatus() == 1 && sameIpUserCount > ipRegLimitParam.getModifyModeThreshold()) {//开启修改下单模式处理
                inviteNewConfig.setSubUserOrderMode(ipRegLimitParam.getOrderModeId());
            }
        }
        Parameter sameDevLimitParameter = parameterService.getParameterByCode(inviteNewConfig.getMarketTeamId(), ParameterConstants.P1019);
        if (sameDevLimitParameter != null && sameDevLimitParameter.getIsEnable() == 1) {
            long sameDevUserCount = queryUserCount(2, inviteNewConfig.getMarketSiteId(), regDeviceId);//同设备注册人数
            Parameter.RegLimitParam devRegLimitParam = JSON.parseObject(sameDevLimitParameter.getValue(), Parameter.RegLimitParam.class);
            if (devRegLimitParam.getRegLimitStatus() == 1 && sameDevUserCount > devRegLimitParam.getRegThreshold()) {//开启注册限制
                throw new BusinessException(500,I18nUtil.getI8nMsg("reg.error.device.outLimit"));
            }
            if (devRegLimitParam.getShuntStatus() == 1 && sameDevUserCount > devRegLimitParam.getShuntThreshold()) {//开启分流处理
                inviteNewConfig.setShuntStatus(true);
                inviteNewConfig.setShuntReason("reg.shunt.reason.devOutLimit");
            }
            if (devRegLimitParam.getModifyOrderModeStatus() == 1 && sameDevUserCount > devRegLimitParam.getModifyModeThreshold()) {//开启修改下单模式处理
                inviteNewConfig.setSubUserOrderMode(devRegLimitParam.getOrderModeId());
            }
        }
    }


    private void verifyValidityMarketSite(MarketSiteBo marketSite) {
        if (marketSite == null) {
            throw new BusinessException(500, I18nUtil.getI8nMsg("reg.error.tenant.notOpen"));
        }
    }

    /**
     * 查询同IP/设备已注册人数
     * @param queryType 查询类型 1、ip；2、设备
     * @param siteId 站点ID
     * @param parameter 注册IP/设备id
     */
    private long queryUserCount(Integer queryType,Integer siteId,String parameter) {
        String redisPrefix;
        SFunction<User, Object> function;
        if (queryType == 1) {//IP查询
            redisPrefix = CacheConstants.SAME_REG_IP_COUNT;
            function = User::getRegIp;
        } else {//设备查询
            redisPrefix = CacheConstants.SAME_REG_DEVICE_COUNT;
            function = User::getRegDeviceId;
        }
        String redisKey = redisPrefix + TenantUtils.getTenantId() + ":" + siteId + ":" + parameter;
        Long userCount = RedisUtils.getLongValue(redisKey);
        if (userCount == null) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(function,parameter).eq(User::getMarketSiteId,siteId);
            userCount = userService.count(queryWrapper);
            RedisUtils.setValue(redisKey,userCount);
        }
        return userCount;
    }
}
