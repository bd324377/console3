package com.console.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.api.dto.CashOutDTO;
import com.console.api.dto.res.CashOutOrderPageRes;
import com.console.api.service.CashOutAPIService;
import com.console.core.entity.Parameter;
import com.console.core.entity.UserProc;
import com.console.core.entity.Wallet;
import com.console.core.entity.WalletProc;
import com.console.core.merger.UserProcMerger;
import com.console.core.merger.WalletProcMerger;
import com.console.core.service.WalletService;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.constants.CacheConstants;
import com.console.framework.constants.CaptchaType;
import com.console.framework.constants.ParameterConstants;
import com.console.framework.domain.BusinessException;
import com.console.framework.domain.ResultCode;
import com.console.framework.request.RequestMember;
import com.console.framework.utils.*;
import com.console.payment.channel.ChannelContext;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.entity.*;
import com.console.payment.merger.CashOutProcMerger;
import com.console.payment.service.BankCardService;
import com.console.payment.service.CashOutOrderService;
import com.console.payment.service.CashOutRuleService;
import com.console.payment.service.ChannelService;
import com.console.user.entity.Agent;
import com.console.user.entity.User;
import com.console.user.model.bo.VipBo;
import com.console.user.service.AgentService;
import com.console.user.service.UserReportService;
import com.console.user.service.UserService;
import com.console.user.service.VipService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Service
public class CashOutAPIServiceImpl extends BaseServiceImpl implements CashOutAPIService {
    @Value("${console.serviceCode}")
    protected String serviceCode;
    @Resource
    private CashOutOrderService cashOutOrderService;
    @Resource
    private WalletService walletService;
    @Resource
    private ChannelService channelService;
    @Resource
    private BankCardService bankCardService;
    @Resource
    private UserService userService;
    @Resource
    private VipService vipService;
    @Resource
    private AgentService agentService;
    @Resource
    private CashOutRuleService cashOutRuleService;
    @Resource
    private UserProcMerger userProcMerger;
    @Resource
    private CashOutProcMerger cashOutProcMerger;
    @Resource
    private WalletProcMerger walletProcMerger;
    @Resource
    private RedisLockRegistry lockRegistry;

    @Override
    public void createCashOutOrder(CashOutDTO.CreateCashOutOrderReq cashOutOrderReq) {
        if (cashOutOrderReq.getAmt().compareTo(BigDecimal.ZERO) <= 0) {//提现金额必须大于0
            throw new BusinessException(500, I18nUtil.getI8nMsg("cashOutOrder.error.create.amount.notGreaterThanZero"));
        }
        RequestMember member = RequestUtils.getRequestMember();
        if (member == null) {
            throw new BusinessException(ResultCode.AUTHENTICATION_FAILED);
        }
        Lock lock = lockRegistry.obtain(CacheConstants.ACQUIRE_LOCK_CREATE_CASH_OUT_ORDER + member.getTenantId() + ":" + member.getId());
        boolean locked = false;
        try {
            locked = lock.tryLock(100, TimeUnit.MILLISECONDS);
            if (!locked) throw new BusinessException(500, I18nUtil.getI8nMsg("paymentOrder.error.create.creating"));
            DynamicTableNameHandler.setTenantId(member.getTenantId());
            cashOutOrderReq.setUserId(member.getId());
            BankCard bankCard = verifyBankCard(member, cashOutOrderReq);//银行卡校验——直接报错
            Wallet wallet = verifyWalletAmt(member,cashOutOrderReq);//校验钱包金额——直接报错
            validateCaptcha(member,wallet,cashOutOrderReq,bankCard);//验证码校验(首次提现)——直接报错
            Channel channel = validateChannel(cashOutOrderReq);//第三方提现渠道判断——直接报错

            List<String> reasonList = new ArrayList<>();
            BigDecimal platCashoutRate = null;
            if (User.UserType.getUserType(member.getUserType()) != User.UserType.TEST) {//测试用户无需校验相关规则
                //以下原因不返回报错，订单进入审核状态
                //判断用户是否设置提现限制
                if (member.getCashOutStatus() != null && member.getCashOutStatus() == 0) {
                    reasonList.add("cashOutOrder.check.reason.personLimit");
                }
                //用户所属VIP/代理等级提现限制
                platCashoutRate = verifyLevelRule(member,wallet,cashOutOrderReq,reasonList);
                //平台提现规则校验
                reasonList.addAll(verifyCashOutRule(cashOutOrderReq,wallet,member));
            }
            //用户归属业务员或所在团队的提现额度限制(后续再判断是否添加)
            CashOutOrder cashOutOrder = buildCashOutOrder(cashOutOrderReq,bankCard,member,reasonList);
            settlemetCashOutFee(cashOutOrder,channel,platCashoutRate);
            settlemetWalletAmt(cashOutOrder,wallet);
            if (User.UserType.getUserType(member.getUserType()) == User.UserType.TEST) {//测试用户无需第三方提款，直接成功
                UpdateWrapper<CashOutOrder> updateWrapper = new UpdateWrapper<>();
                updateWrapper.lambda()
                        .eq(CashOutOrder::getId,cashOutOrder.getId())
                        .set(CashOutOrder::getStatus,CashOutOrder.CashOutStatus.SUCCESS)
                        .set(CashOutOrder::getCompleteStatus,1);
                if (cashOutOrderService.update(updateWrapper)) {
                    //发起支付记录
                    CashOutProc startCashOutProc = new CashOutProc(cashOutOrder, CashOutProc.ProcType.ONLINE_CASH_OUT_SUCCESS);
                    cashOutProcMerger.submit(startCashOutProc);
                }
            } else {
                if (cashOutOrder.getStatus() == CashOutOrder.CashOutStatus.CASHOUTING) {//创建第三方提现订单
                    ChannelResult channelResult = ChannelContext.createCashOutOrder(cashOutOrder, channel, bankCard);
                    UpdateWrapper<CashOutOrder> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.lambda().eq(CashOutOrder::getId, cashOutOrder.getId());
                    if (channelResult.isSuccess()) {//成功
                        cashOutOrder.setMerchantOrderNo(channelResult.getMerchantOrderNo());
                        cashOutOrder.setTransactionId(channelResult.getOrderNo());
                        updateWrapper.lambda()
                                .set(CashOutOrder::getTransactionId, channelResult.getOrderNo())
                                .set(CashOutOrder::getMerchantOrderNo, channelResult.getMerchantOrderNo());
                        cashOutOrderService.update(updateWrapper);
                    } else {
                        updateWrapper.lambda()
                                .set(CashOutOrder::getStatus, CashOutOrder.CashOutStatus.PENDING)
                                .set(CashOutOrder::getErrorMsg, channelResult.getMessage())
                                .set(CashOutOrder::getRemarks, "创建第三方支付订单异常");
                        cashOutOrderService.update(updateWrapper);
                        if (channelResult.getMessage().contains("accountNo")) {
                            UpdateWrapper<BankCard> bankCardUpdateWrapper = new UpdateWrapper<>();
                            bankCardUpdateWrapper.lambda()
                                    .eq(BankCard::getId, cashOutOrderReq.getBankCardId())
                                    .set(BankCard::getRemarks, "银行卡信息错误");
                            bankCardService.update(bankCardUpdateWrapper);
                            throw new BusinessException(501, channelResult.getMessage());
                        }
                        //失败
                        throw new BusinessException(500, I18nUtil.getI8nMsg("cashOutOrder.error.create.channel.busy"));
                    }
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ResultCode.LOCK_NOT_RELEASED);
        } finally {
            if (locked) lock.unlock();
        }
    }

    /**
     * 分页获取提现订单
     * @param searchCashOutOrderReq  查询条件
     */
    @Override
    public CashOutOrderPageRes getCashOutOrderRecords(CashOutDTO.SearchCashOutOrderReq searchCashOutOrderReq) {
        Page<CashOutOrder> page = new Page<>(searchCashOutOrderReq.getCurrent(), searchCashOutOrderReq.getSize());
        QueryWrapper<CashOutOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,order_time,order_amt,transaction_id,status").lambda()
                .eq(CashOutOrder::getUserId,RequestUtils.getRequestMember().getId())
                .eq(searchCashOutOrderReq.getStatus() != null,CashOutOrder::getStatus,searchCashOutOrderReq.getStatus())
                .between(searchCashOutOrderReq.getStartSearchTime() != null && searchCashOutOrderReq.getEndSearchTime() != null,
                        CashOutOrder::getOrderTime,searchCashOutOrderReq.getStartSearchTime(),searchCashOutOrderReq.getEndSearchTime())
                .orderByDesc(CashOutOrder::getId);
        CashOutOrderPageRes cashOutOrderPageRes = new CashOutOrderPageRes(cashOutOrderService.page(page,queryWrapper));
        if (searchCashOutOrderReq.getCurrent() == 1) {
            if (cashOutOrderPageRes.getTotal() == 0) {
                cashOutOrderPageRes.setTotalCashOutAmt(BigDecimal.ZERO);
                cashOutOrderPageRes.setTotalPendingReviewAmt(BigDecimal.ZERO);
            } else {
                queryWrapper.select("COALESCE ( SUM( CASE WHEN status = 5 OR status = 7 THEN order_amt END ), 0 ) AS cash_out_amt",
                                "COALESCE ( SUM( CASE WHEN status = 1 OR status = 2 THEN order_amt END ), 0 ) AS pending_review_amt").lambda()
                        .eq(CashOutOrder::getUserId, RequestUtils.getRequestMember().getId())
                        .eq(searchCashOutOrderReq.getStatus() != null, CashOutOrder::getStatus, searchCashOutOrderReq.getStatus())
                        .between(searchCashOutOrderReq.getStartSearchTime() != null && searchCashOutOrderReq.getEndSearchTime() != null,
                                CashOutOrder::getOrderTime, searchCashOutOrderReq.getStartSearchTime(), searchCashOutOrderReq.getEndSearchTime())
                        .orderByDesc(CashOutOrder::getId);
                CashOutOrder cashOutOrder = cashOutOrderService.getOne(queryWrapper);
                cashOutOrderPageRes.setTotalCashOutAmt(cashOutOrder.getCashOutAmt());
                cashOutOrderPageRes.setTotalPendingReviewAmt(cashOutOrder.getPendingReviewAmt());
            }
        }
        return cashOutOrderPageRes;
    }


    /**
     * 获取银行卡信息并校验
     */
    private BankCard verifyBankCard(RequestMember requestMember, CashOutDTO.CreateCashOutOrderReq cashOutOrderReq) {
        BankCard bankCard;
        if (cashOutOrderReq.getBankCardId() != null) {
            bankCard = bankCardService.getById(cashOutOrderReq.getBankCardId());
            if (bankCard == null) {
                throw new BusinessException(500, I18nUtil.getI8nMsg("cashOutOrder.error.bankCard.notBind"));
            }
            if (!Objects.equals(bankCard.getUserId(), requestMember.getId())) {
                throw new BusinessException(500, I18nUtil.getI8nMsg("cashOutOrder.error.bankCard.notSelf"));
            }
            return bankCard;
        } else {
            if (!StringUtils.hasText(cashOutOrderReq.getBankCardCode())) {
                throw new BusinessException(500, I18nUtil.getI8nMsg("cashOutOrder.error.bankCard.notBind"));
            }
            QueryWrapper<BankCard> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(BankCard::getBankCardCode,cashOutOrderReq.getBankCardCode()).eq(BankCard::getUserId,requestMember.getId());
            bankCard = bankCardService.getOne(queryWrapper);
            if (bankCard == null) {
                bankCard = new BankCard();
                bankCard.setUserId(requestMember.getId());
                bankCard.setBankCardCode(cashOutOrderReq.getBankCardCode());
                bankCard.setStatus(BankCard.BankCardStatus.CASHOUTING);
                bankCard.setType(cashOutOrderReq.getBankCardType());
                bankCard.setBindTime(LocalDateTime.now());
                bankCard.setUserName(cashOutOrderReq.getRealName());
                bankCard.setPhone(cashOutOrderReq.getPhone());
                try {
                    if (bankCardService.save(bankCard)) {//添加用户绑卡记录
                        UserProc userProc = new UserProc(requestMember.getId(), requestMember.getTenantId(), UserProc.ProcType.BIND_BANK_CARD);
                        userProcMerger.submit(userProc);
                    }
                } catch (DuplicateKeyException e) {
                    String uniqueKey = UniqueKeyUtils.extractUniqueKey(e);
                    if ("uk_bank_card_code".equals(uniqueKey)) {
                        throw new BusinessException(500, I18nUtil.getI8nMsg("bankCard.error.bind.alreadyExists"));
                    }
                    if ("uk_user_bank_card".equals(uniqueKey)) {
                        throw new BusinessException(500, I18nUtil.getI8nMsg("bankCard.error.bind.typeAlreadyExists"));
                    }
                }
            }
            return bankCard;
        }
    }

    /** 钱包信息校验 **/
    private Wallet verifyWalletAmt(RequestMember requestMember, CashOutDTO.CreateCashOutOrderReq cashOutOrderReq) {
        QueryWrapper<Wallet> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("user_id,balance,cash_able_amt,cash_out_times,last_cash_out_time,day_cash_out_amt,cash_out_amt,day_cash_out_amt,recharge_amt,order_amt")
                .lambda().eq(Wallet::getUserId,requestMember.getId()).eq(Wallet::getWalletType,cashOutOrderReq.getWalletType());
        Wallet wallet = walletService.getOne(queryWrapper);
        if (wallet == null) {
            throw new BusinessException(500, I18nUtil.getI8nMsg("cashOutOrder.error.create.cashAbleAmt.less"));//可提款金额不足
        }
        if (!StringUtils.hasText(requestMember.getCashOutPwd()) || !cashOutOrderReq.getCashOutPwd().equals(requestMember.getCashOutPwd())) {
            throw new BusinessException(500,I18nUtil.getI8nMsg("cashOutOrder.error.create.cahsOutPwd.error"));
        }
        if (wallet.getBalance().compareTo(cashOutOrderReq.getAmt()) < 0 || wallet.getCashAbleAmt().compareTo(cashOutOrderReq.getAmt()) < 0) {
            throw new BusinessException(500, I18nUtil.getI8nMsg("cashOutOrder.error.create.cashAbleAmt.less"));//可提款金额不足
        }
        return wallet;
    }

    /** 提现验证码校验 **/
    private void validateCaptcha(RequestMember member,Wallet wallet, CashOutDTO.CreateCashOutOrderReq cashOutOrderReq,BankCard bankCard) {
        if ((wallet.getCashOutTimes() != null && wallet.getCashOutTimes() > 0) || (bankCard.getStatus() != null && bankCard.getStatus() == BankCard.BankCardStatus.ACTIVIVE)) return;
        //查询首次提现是否需要校验验证码参数
        Parameter captchaParameter = parameterService.getParameterByCode(member.getTeamId(), ParameterConstants.P1022);//银行卡首次提现是否需要（短信验证）
        if (captchaParameter == null || captchaParameter.getIsEnable() == 0) return;
        if (!StringUtils.hasText(cashOutOrderReq.getCaptcha())) {
            throw new BusinessException(500,I18nUtil.getI8nMsg("cashOutOrder.error.create.captcha.notMatch"));
        }
        RedisUtils.validateCaptcha(cashOutOrderReq.getCaptcha(),
                cashOutOrderReq.getPhone() + ":" + CaptchaType.CASH_OUT.getKey(),member.getTenantId());
        if (member.getPhoneStatus() == null || member.getPhoneStatus() != 1) {//未绑定手机号则直接绑定手机号
            try {
                UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
                userUpdateWrapper.lambda().eq(User::getId,member.getId())
                        .set(User::getPhone,cashOutOrderReq.getPhone())
                        .set(User::getPhoneStatus,1);
                userService.update(userUpdateWrapper);
            } catch (DuplicateKeyException exception) {
                log.error("触发唯一索引报错，用户：【{}】，使用的提现手机号：【{}】已被使用",member.getId(),cashOutOrderReq.getPhone());
                throw new BusinessException(500,I18nUtil.getI8nMsg("user.error.update.phone.alreadyBind"));
            }
            member.setPhoneStatus(1);
            member.setPhone(cashOutOrderReq.getPhone());
            TokenUtils.updateUserToken(member);
        }
    }

    /** 校验渠道信息 **/
    private Channel validateChannel(CashOutDTO.CreateCashOutOrderReq cashOutOrderReq) {
        Channel channel = channelService.getChannelById(cashOutOrderReq.getChannelId());
        if (channel == null) {
            throw new BusinessException(500, I18nUtil.getI8nMsg("cashOutOrder.error.create.channel.expire"));
        }
        //支付状态判断
        if (channel.getCashOutStatus() != 1) {
            throw new BusinessException(500,I18nUtil.getI8nMsg("cashOutOrder.error.create.channel.closed"));
        }
        if (cashOutOrderReq.getAmt().compareTo(channel.getMinCashOutAmt()) < 0) {//最小提现金额限制
            throw new BusinessException(500, I18nUtil.getI8nMsg("cashOutOrder.error.create.channel.lessMinCashOutAmt"));
        }
        if (cashOutOrderReq.getAmt().compareTo(channel.getMaxCashOutAmt()) > 0) {//最大提现金额限制
            throw new BusinessException(500, I18nUtil.getI8nMsg("cashOutOrder.error.create.channel.greaterMaxCashOutAmt"));
        }
        return channel;
    }
    /** 等级相关规则校验 **/
    private BigDecimal verifyLevelRule(RequestMember member, Wallet wallet, CashOutDTO.CreateCashOutOrderReq cashOutOrderReq,List<String> reasonList) {
        if (cashOutOrderReq.getWalletType() == Wallet.WalletType.USER_WALLET) {//用户钱包
            VipBo vip = vipService.getVipById(member.getVipId());
            if (vip.getCashOutStatus() == 0) {
                reasonList.add("cashOutOrder.check.reason.levelLimit");
            }
            if (wallet.getDayCashOutAmt().add(cashOutOrderReq.getAmt()).compareTo(vip.getDayCashOutLimit()) > 0) {
                reasonList.add("cashOutOrder.check.reason.levelDayLimit");
            }
            return vip.getCashOutRate();
        } else {//代理钱包
            Agent agent = agentService.getAgentById(member.getAgentId());
            if (agent.getCashOutStatus() == 1) {
                reasonList.add("cashOutOrder.check.reason.levelLimit");
            }
            if (wallet.getCashOutAmt().add(cashOutOrderReq.getAmt()).compareTo(agent.getDayCashOutLimit()) > 0) {
                reasonList.add("cashOutOrder.check.reason.levelDayLimit");
            }
            return agent.getCashOutRate();
        }
    }
    /** 平台提现规则校验 **/
    private List<String> verifyCashOutRule(CashOutDTO.CreateCashOutOrderReq cashOutOrderReq, Wallet wallet, RequestMember requestMember) {
        if (cashOutOrderReq.getWalletType() == Wallet.WalletType.USER_WALLET) {
            return verifyUserCashOutRule(cashOutOrderReq,wallet,requestMember);
        } else {
            return verifyAgentCashOutRule(cashOutOrderReq,wallet,requestMember);
        }
    }

    /** 用户提现规则校验 **/
    private List<String> verifyUserCashOutRule(CashOutDTO.CreateCashOutOrderReq cashOutOrderReq, Wallet wallet, RequestMember requestMember) {
        List<String> reasonList = new ArrayList<>();
        CashOutRule cashOutRule = cashOutRuleService.getCashOutRule(requestMember.getTeamId(), CashOutRule.RuleType.USER_AMT_AUTOMATIC);//自动出款阈值
        if (cashOutRule != null && cashOutRule.getRuleStatus() == 1) {//自动出款阈值判断
            BigDecimal limitAmt = new BigDecimal(cashOutRule.getRuleValue());
            if (cashOutOrderReq.getAmt().compareTo(limitAmt) > 0) {//大于自动提款限额则订单需要审批后才能出款
                reasonList.add("cashOutOrder.check.reason.outMaxCashOutAmtLimit");
            }
        }
        cashOutRule = cashOutRuleService.getCashOutRule(requestMember.getTeamId(), CashOutRule.RuleType.USER_RECHARGE_CASHOUT_RATE);
        if (cashOutRule != null && cashOutRule.getRuleStatus() == 1) {//充提比例阈值判断
            BigDecimal rechargeAndCashOutRate;
            if (wallet.getRechargeAmt().compareTo(BigDecimal.ZERO) == 0) {
                rechargeAndCashOutRate = new BigDecimal("0");
            } else if (wallet.getCashOutAmt().compareTo(BigDecimal.ZERO) == 0) {
                rechargeAndCashOutRate = wallet.getRechargeAmt();
            } else {
                rechargeAndCashOutRate = wallet.getRechargeAmt().divide(wallet.getCashOutAmt(),2, RoundingMode.HALF_UP);
            }
            BigDecimal limitRate = new BigDecimal(cashOutRule.getRuleValue());
            if (rechargeAndCashOutRate.compareTo(limitRate) < 0) {//小于充提比例阈值则
                cashOutOrderReq.setRiskValue(cashOutOrderReq.getRiskValue() + 1);
                reasonList.add("cashOutOrder.check.reason.outRechargeAndCashOutRate");
            }
        }
        cashOutRule = cashOutRuleService.getCashOutRule(requestMember.getTeamId(), CashOutRule.RuleType.USER_ORDERED_RECHARGE_RATE);
        if (cashOutRule != null && cashOutRule.getRuleStatus() == 1) {//下单金额与充值金额比例阈值判断
            BigDecimal orderedAndRechargeRate;
            if (wallet.getOrderAmt().compareTo(BigDecimal.ZERO) == 0) {
                orderedAndRechargeRate = new BigDecimal("0");
            } else if (wallet.getRechargeAmt().compareTo(BigDecimal.ZERO) == 0) {
                orderedAndRechargeRate = wallet.getOrderAmt();
            } else {
                orderedAndRechargeRate = wallet.getOrderAmt().divide(wallet.getRechargeAmt(),2,RoundingMode.HALF_UP);
            }
            BigDecimal limitRate = new BigDecimal(cashOutRule.getRuleValue());
            if (orderedAndRechargeRate.compareTo(limitRate) < 0) {//小于下单与充值比例阈值则
                cashOutOrderReq.setRiskValue(cashOutOrderReq.getRiskValue() + 1);
                reasonList.add("cashOutOrder.check.reason.outOrderAndRachargeRate");
            }
        }
        return reasonList;
    }

    /**
     * 代理提现规则校验
     */
    private List<String> verifyAgentCashOutRule(CashOutDTO.CreateCashOutOrderReq cashOutOrderReq,Wallet wallet,RequestMember requestMember) {
        List<String> reasonList = new ArrayList<>();
        CashOutRule cashOutRule = cashOutRuleService.getCashOutRule(requestMember.getTeamId(), CashOutRule.RuleType.AGENT_AMT_AUTOMATIC);//自动出款阈值
        if (cashOutRule != null && cashOutRule.getRuleStatus() == 1) {//自动出款阈值判断
            BigDecimal limitAmt = new BigDecimal(cashOutRule.getRuleValue());
            if (limitAmt.compareTo(cashOutOrderReq.getAmt()) < 0) {//大于自动提款限额则订单需要审批后才能出款
                reasonList.add("cashOutOrder.check.reason.outMaxCashOutAmtLimit");
            }
        }
        cashOutRule = cashOutRuleService.getCashOutRule(requestMember.getTeamId(), CashOutRule.RuleType.SUB_USER_USER_ORDERED_RECHARGE_RATE);
        if (cashOutRule != null && cashOutRule.getRuleStatus() == 1) {//直属下级下单和充值比率阈值
            Wallet subUserWalletReport = walletService.getSubUserWalletReport(requestMember.getId());
            BigDecimal subOrderedAndRechargeRate;
            if (subUserWalletReport == null) {
                subOrderedAndRechargeRate = new BigDecimal("0");
            } else if (subUserWalletReport.getOrderAmt().compareTo(BigDecimal.ZERO) == 0) {
                subOrderedAndRechargeRate = new BigDecimal("0");
            } else if (wallet.getRechargeAmt().compareTo(BigDecimal.ZERO) == 0) {
                subOrderedAndRechargeRate = subUserWalletReport.getOrderAmt();
            } else {
                subOrderedAndRechargeRate = subUserWalletReport.getOrderAmt().divide(subUserWalletReport.getRechargeAmt(),2,RoundingMode.HALF_UP);
            }
            BigDecimal limitRate = new BigDecimal(cashOutRule.getRuleValue());
            if (subOrderedAndRechargeRate.compareTo(limitRate) < 0) {//小于订单和充值比例阈值则
                cashOutOrderReq.setRiskValue(cashOutOrderReq.getRiskValue() + 1);
                reasonList.add("cashOutOrder.check.reason.outOrderAndRachargeRate");
            }
        }
        return reasonList;
    }

    private CashOutOrder buildCashOutOrder(CashOutDTO.CreateCashOutOrderReq cashOutOrderReq,BankCard bankCard,RequestMember member,List<String> reasonList) {
        CashOutOrder cashOutOrder = new CashOutOrder();
        cashOutOrder.setId(IdWorker.getId());
        cashOutOrder.setMarketSiteId(member.getSiteId());
        cashOutOrder.setMarketTeamId(member.getTeamId());
        cashOutOrder.setMarketerId(member.getMarketerId());
        cashOutOrder.setOrderAmt(cashOutOrderReq.getAmt().setScale(2,RoundingMode.HALF_UP));
        cashOutOrder.setOrderTime(LocalDateTime.now());
        cashOutOrder.setWalletType(cashOutOrderReq.getWalletType());
        cashOutOrder.setBankCardCode(bankCard.getBankCardCode());
        cashOutOrder.setUserId(cashOutOrderReq.getUserId());
        cashOutOrder.setChannelId(cashOutOrderReq.getChannelId());
        cashOutOrder.setRiskValue(cashOutOrderReq.getRiskValue());
        if (!ObjectUtils.isEmpty(reasonList)) {
            cashOutOrder.setCheckReasons(JSON.toJSONString(reasonList));
            cashOutOrder.setStatus(CashOutOrder.CashOutStatus.PENDING);
        } else {
            cashOutOrder.setStatus(CashOutOrder.CashOutStatus.CASHOUTING);
        }
        cashOutOrder.setMerchantOrderNo(serviceCode + "_" + member.getTenantId() + "_" + member.getSiteId() + "_" + cashOutOrder.getId());
        return cashOutOrder;
    }

    /** 结算提现手续费 **/
    private void settlemetCashOutFee(CashOutOrder cashOutOrder,Channel channel,BigDecimal platCashOutRate) {//手续费计算
        //1、平台手续费（用户到手金额需扣除该费用）
        if (platCashOutRate != null && platCashOutRate.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal platFee = cashOutOrder.getOrderAmt().multiply(platCashOutRate.divide(new BigDecimal(100),2,RoundingMode.HALF_UP));
            cashOutOrder.setFee(platFee);
        } else {
            cashOutOrder.setFee(BigDecimal.ZERO);
        }
        //2、第三方渠道手续费（由平台承担，与用户无关）
        if (channel.getCashOutFeeRate() != null && channel.getCashOutFeeRate().compareTo(BigDecimal.ZERO) > 0) {//存在代付手续费
            BigDecimal channelFee = cashOutOrder.getOrderAmt().multiply(channel.getCashOutFeeRate().divide(new BigDecimal(100),2,RoundingMode.HALF_UP));
            cashOutOrder.setChannelFee(channelFee);
        } else {
            cashOutOrder.setChannelFee(BigDecimal.ZERO);
        }
    }

    /** 结算钱包金额 **/
    private void settlemetWalletAmt(CashOutOrder cashOutOrder, Wallet wallet) {
        UpdateWrapper<Wallet> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(Wallet::getId,cashOutOrder.getUserId()).eq(Wallet::getWalletType,cashOutOrder.getWalletType());
        BigDecimal surpAmt = wallet.getBalance().subtract(cashOutOrder.getOrderAmt());
        updateWrapper.setSql("balance = balance - " + cashOutOrder.getOrderAmt())
                .setSql("cash_able_amt = cash_able_amt - " + cashOutOrder.getOrderAmt());
        if (surpAmt.compareTo(BigDecimal.ZERO) <= 0) {
            updateWrapper.setSql("cash_able_amt = 0").setSql("to_be_order_amt = 0");
        }
        cashOutOrder.setBalance(surpAmt);
        wallet.setBalance(surpAmt);
        wallet.setCashAbleAmt(wallet.getCashAbleAmt().subtract(cashOutOrder.getOrderAmt()));
        if (cashOutOrder.getWalletType() == Wallet.WalletType.USER_WALLET) {//用户钱包   用户钱包提现需判断是否切换用户下单模式
            updateUserOrderMode();//判断是否切换用户下单模式
        }
        if (cashOutOrderService.save(cashOutOrder) && walletService.update(updateWrapper)) {//插入提现订单和更新钱包信息
            //发起支付记录
            CashOutProc startCashOutProc = new CashOutProc(cashOutOrder, CashOutProc.ProcType.START_ONLINE_CASH_OUT);
            cashOutProcMerger.submit(startCashOutProc);
            //钱包变更记录
            WalletProc walletProc = new WalletProc(cashOutOrder.getId(), cashOutOrder.getUserId(), cashOutOrder.getWalletType(), 2, WalletProc.ProcType.ONLINE_CASH_OUT, wallet.getBalance(), cashOutOrder.getOrderAmt(),null);
            walletProcMerger.submit(walletProc);
        }
    }

    private void updateUserOrderMode() {
//        Parameter parameter_1033 = getParameterByCode(ParameterConstants.P1033);
//        if (wallet.getOrderResultAmt().compareTo(BigDecimal.ONE) >= 0 && parameter_1033 != null && parameter_1033.getIsEnable() == 1 && StringUtils.hasText(parameter_1033.getValue())) {
//            if (wallet.getOrderMode() != 3 && !wallet.getOrderMode().toString().equals(parameter_1033.getValue())) {
//                updateWrapper.lambda()
//                        .set(Wallet::getOrderMode,Integer.valueOf(parameter_1033.getValue()))//0、系统默认轻杀模式
//                        .set(Wallet::getOrderModeIndex,1)
//                        .set(Wallet::getSwitchIndexTime,DateUtils.getCurrentTimestamp());
//            }
//        }
    }
}
