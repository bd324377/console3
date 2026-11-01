package com.console.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.api.dto.PaymentDTO;
import com.console.api.service.PaymentAPIService;
import com.console.core.entity.Parameter;
import com.console.core.model.bo.MarketerBo;
import com.console.core.service.MarketerService;
import com.console.framework.constants.CacheConstants;
import com.console.framework.constants.ParameterConstants;
import com.console.framework.domain.BusinessException;
import com.console.framework.domain.ResultCode;
import com.console.framework.request.RequestMember;
import com.console.framework.utils.I18nUtil;
import com.console.framework.utils.RequestUtils;
import com.console.payment.channel.ChannelContext;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.entity.Channel;
import com.console.payment.entity.PaymentOrder;
import com.console.payment.entity.PaymentProc;
import com.console.payment.merger.PaymentProcMerger;
import com.console.payment.service.ChannelService;
import com.console.payment.service.PaymentOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Service
public class PaymentAPIServiceImpl extends BaseServiceImpl implements PaymentAPIService {
    @Value("${console.serviceCode}")
    protected String serviceCode;
    @Resource
    private PaymentOrderService paymentOrderService;
    @Resource
    private ChannelService channelService;
    @Resource
    private MarketerService marketerService;
    @Resource
    private RedisLockRegistry lockRegistry;
    @Resource
    private PaymentProcMerger paymentProcMerger;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public String createPaymentOrder(PaymentDTO.CreatePayOrderReq createPayOrderReq) {
        RequestMember member = RequestUtils.getRequestMember();
        if (member == null) {
            throw new BusinessException(ResultCode.AUTHENTICATION_FAILED);
        }
        Lock lock = lockRegistry.obtain(CacheConstants.ACQUIRE_LOCK_CREATE_PAY_ORDER + member.getTenantId() + ":" + member.getId());
        boolean locked = false;
        try {
            locked = lock.tryLock(100, TimeUnit.MILLISECONDS);
            if (!locked) throw new BusinessException(500, I18nUtil.getI8nMsg("paymentOrder.error.create.creating"));
            createPayOrderReq.setUserId(member.getId());
            checkFirstRechargeParam(createPayOrderReq,member);//校验首充翻倍参数
            Channel channel = channelService.getChannelById(createPayOrderReq.getChannelId());
            PaymentOrder  paymentOrder  = buildPaymentOrder(createPayOrderReq,channel);//组装支付订单信息
            settlemetPaymentFee(paymentOrder,channel,member.getTeamId());
            paymentOrder.setMerchantOrderNo(serviceCode + "_" + member.getTenantId() + "_" + member.getSiteId() + "_" + paymentOrder.getId());
            ChannelResult channelResult = ChannelContext.createPayOrder(paymentOrder,channel);//向第三方支付渠道发起创建订单请求
            if (channelResult.isSuccess()) {//支付渠道创建订单成功
                paymentOrder.setPayUrl(channelResult.getPayUrl());
                paymentOrder.setTransactionId(channelResult.getOrderNo());
                paymentOrder.setMerchantOrderNo(channelResult.getMerchantOrderNo());
                if (paymentOrderService.save(paymentOrder)) {//插入支付订单信息
                    PaymentProc createProc = new PaymentProc(paymentOrder, PaymentProc.ProcType.START_ONLINE_PAYMENT);
                    paymentProcMerger.submit(createProc);
                    return paymentOrder.getPayUrl();
                }
            }
            PaymentProc createProc = new PaymentProc(paymentOrder, PaymentProc.ProcType.START_ONLINE_PAYMENT_FAILED);
            createProc.setRemark(channelResult.getMessage());
            paymentProcMerger.submit(createProc);
            log.info("创建第三方渠道支付订单失败：" + channelResult.getMessage());
            //支付渠道订单创建失败
            throw new BusinessException(500,channelResult.getMessage());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ResultCode.LOCK_NOT_RELEASED);
        } finally {
            if (locked) lock.unlock();
        }
    }

    @Override
    public IPage<PaymentOrder> getPaymentOrderRecords(PaymentDTO.SearchPayOrderRes searchPayOrderRes) {
        Page<PaymentOrder> page = new Page<>(searchPayOrderRes.getCurrent(), searchPayOrderRes.getSize());
        QueryWrapper<PaymentOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("order_time,transaction_id,order_amt,status").lambda()
                .eq(PaymentOrder::getUserId,RequestUtils.getUser(RequestMember.class).getId())
                .eq(searchPayOrderRes.getStatus() != null,PaymentOrder::getStatus,searchPayOrderRes.getStatus())
                .between(searchPayOrderRes.getStartSearchTime() != null && searchPayOrderRes.getEndSearchTime() != null,PaymentOrder::getOrderTime,searchPayOrderRes.getStartSearchTime(),searchPayOrderRes.getEndSearchTime())
                .orderByDesc(PaymentOrder::getId);
        return paymentOrderService.page(page,queryWrapper);
    }

    /** 判断首充参数 **/
    private void checkFirstRechargeParam(PaymentDTO.CreatePayOrderReq createPayOrderReq, RequestMember requestMember) {
        Parameter firstRechargeParameter = parameterService.getParameterByCode(requestMember.getTeamId(), ParameterConstants.P1020);
        if (firstRechargeParameter != null && firstRechargeParameter.getIsEnable() == 1) {//有开启是否充值翻倍功能
            QueryWrapper<PaymentOrder> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("id").lambda()
                    .eq(PaymentOrder::getUserId,requestMember.getId())
                    .eq(PaymentOrder::getOrderType,1);
            long count = paymentOrderService.count(queryWrapper);
            BigDecimal limitAmt = new BigDecimal(firstRechargeParameter.getValue());
            if (count == 0 && createPayOrderReq.getAmount().compareTo(limitAmt) <= 0) {//符合首充翻10倍需求
                createPayOrderReq.setAmount(createPayOrderReq.getAmount().multiply(new BigDecimal(10)));
            }
        }
    }

    /**
     * 渠道判断和组装支付订单信息
     * @param createPayOrderReq 支付信息
     * @param channel           群岛信息
     */
    private PaymentOrder buildPaymentOrder(PaymentDTO.CreatePayOrderReq createPayOrderReq, Channel channel) {
        if (channel == null) {
            throw new BusinessException(500, I18nUtil.getI8nMsg("paymentOrder.error.create.channel.expire"));
        }
        //支付状态判断
        if (channel.getPayStatus() != 1) {
            throw new BusinessException(500,I18nUtil.getI8nMsg("paymentOrder.error.create.channel.closed"));
        }
        //支付实名状态判断
        if (channel.getRealNamePayStatus() == 1 && (!StringUtils.hasText(createPayOrderReq.getPayerIdNo()) || !StringUtils.hasText(createPayOrderReq.getPayerName()))) {
            throw new BusinessException(500,I18nUtil.getI8nMsg("paymentOrder.error.create.channel.needRealNamePay"));
        }
        if (createPayOrderReq.getAmount().compareTo(channel.getMinPayAmt()) < 0) {//最小支付金额限制
            throw new BusinessException(500, I18nUtil.getI8nMsg("paymentOrder.error.create.channel.lessMinPayAmt"));
        }
        if (createPayOrderReq.getAmount().compareTo(channel.getMaxPayAmt()) > 0) {//最大支付金额限制
            throw new BusinessException(500, I18nUtil.getI8nMsg("paymentOrder.error.create.channel.greaterMaxPayAmt"));
        }

        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setId(IdWorker.getId());
        paymentOrder.setOrderType(1);
        paymentOrder.setChannelId(createPayOrderReq.getChannelId());
        paymentOrder.setOrderAmt(createPayOrderReq.getAmount());
        paymentOrder.setActivityId(createPayOrderReq.getActivityId());
        paymentOrder.setUserId(createPayOrderReq.getUserId());
        paymentOrder.setOrderTime(LocalDateTime.now());
        paymentOrder.setStatus(PaymentOrder.PaymentStatus.PAYING);
        paymentOrder.setIp(RequestUtils.getRequestIp());                    //设置充值ip
        paymentOrder.setToBeOrderAmt(paymentOrder.getOrderAmt().multiply(channel.getToBeOrderRate()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        return paymentOrder;
    }

    private void settlemetPaymentFee(PaymentOrder paymentOrder,Channel channel,Integer marketerId) {
        MarketerBo marketerBo = marketerService.getMarketer(marketerId);
        if (marketerBo.getRechargeRate() != null && marketerBo.getRechargeRate().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal platFee = paymentOrder.getOrderAmt().multiply(marketerBo.getRechargeRate().divide(new BigDecimal(100),2,RoundingMode.HALF_UP));
            paymentOrder.setFee(platFee);
        }
        if (channel.getPayFeeRate() != null && channel.getPayFeeRate().compareTo(BigDecimal.ZERO) > 0) {
            paymentOrder.setChannelFee(paymentOrder.getOrderAmt().multiply(channel.getPayFeeRate()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        }
    }
}
