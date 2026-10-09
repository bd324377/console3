package com.console.payment.channel.strategy;

import com.console.framework.constants.CacheConstants;
import com.console.framework.domain.BusinessException;
import com.console.framework.domain.ResultCode;
import com.console.framework.utils.TenantUtils;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.channel.strategy.fourz.res.BalanceRes;
import com.console.payment.entity.BankCard;
import com.console.payment.entity.CashOutOrder;
import com.console.payment.entity.Channel;
import com.console.payment.entity.PaymentOrder;
import com.console.payment.service.CashOutOrderService;
import com.console.payment.service.ChannelService;
import com.console.payment.service.PaymentOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.redis.util.RedisLockRegistry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 支付渠道统一扩展点。具体渠道只负责协议转换和远程请求，
 * 本地订单持久化、状态机和回调幂等由应用服务统一处理。
 */
@Slf4j
public class ChannelStrategy {
    @Value("${console.serviceCode}")
    protected String serviceCode;
    @Resource
    private PaymentOrderService paymentOrderService;
    @Resource
    private CashOutOrderService cashOutOrderService;
    @Resource
    private ChannelService channelService;
    @Resource
    private RedisLockRegistry redisLockRegistry;

    public ChannelResult createPayOrder(PaymentOrder order, Channel channel) {
        throw unsupported();
    }

    public ChannelResult queryPayOrder(String merchantOrderNo, Channel channel) {
        throw unsupported();
    }

    public ChannelResult createCashOutOrder(CashOutOrder order, Channel channel, BankCard bankCard) {
        throw unsupported();
    }

    public ChannelResult queryCashOutOrder(String merchantOrderNo, Channel channel) {
        throw unsupported();
    }

    public Object queryBalance(Channel channel) {
        throw unsupported();
    }

    public ChannelResult parsePaymentCallback(Object paymentCallBack) {
        throw unsupported();
    }

    public ChannelResult parseCashOutCallback(Object cashOutCallBack) {
        throw unsupported();
    }

    private UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("payment channel operation is not supported");
    }

    public Channel getPaymentChannel(Integer siteId, String merchantId) {
        return channelService.getChannelByMerchantId(siteId,merchantId);
    }

    /**
     * 支付回调处理
     * @param channelResult 回调结果
     */
    public boolean payCallBackHandle(ChannelResult channelResult) {
        String lockKey = CacheConstants.ACQUIRE_LOCK_PAYMENT_ORDER_CALL_BACK + TenantUtils.getTenantId() + ":" + channelResult.getOrderNo();
        Lock lock = redisLockRegistry.obtain(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(100, TimeUnit.MILLISECONDS);
            if (locked) {
                PaymentOrder paymentOrder = paymentOrderService.getById(channelResult.extractOrderIdByMerchantOrderNo());
                if (paymentOrder == null || paymentOrder.getStatus() != PaymentOrder.PaymentStatus.PAYING) return false;//订单不存在或者订单状态不是待支付则直接返回失败，不做处理
                BigDecimal callBackAmt = channelResult.getAmtUnit() == Channel.AmtUnit.FEN ? (new BigDecimal(channelResult.getAmount()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)) : new BigDecimal(channelResult.getAmount());
                if (paymentOrder.getOrderAmt().compareTo(callBackAmt) != 0) {
                    return false;//金额不一致
                }
                paymentOrderService.handlePaymentOrder(paymentOrder, channelResult);
                return true;
            }
        } catch (InterruptedException e) {
            log.error("获取更新用户缓存余额分布式锁失败：{}",e.getMessage());//暂不做处理
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
        throw new BusinessException(ResultCode.LOCK_NOT_RELEASED);
    }
    /**
     * 提现回调处理
     * @param channelResult 回调结果
     */
    public boolean cashOutCallBackHandle(ChannelResult channelResult) {
        String lockKey = CacheConstants.ACQUIRE_LOCK_CASH_OUT_ORDER_CALL_BACK + TenantUtils.getTenantId() + ":" + channelResult.getOrderNo();
        Lock lock = redisLockRegistry.obtain(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(100, TimeUnit.MILLISECONDS);
            if (locked) {
                CashOutOrder cashOutOrder = cashOutOrderService.getById(channelResult.extractOrderIdByMerchantOrderNo());
                if (cashOutOrder == null || cashOutOrder.getStatus() != CashOutOrder.CashOutStatus.CASHOUTING) return false;//订单不存在或者订单状态不是待支付则直接返回失败，不做处理
                BigDecimal callBackAmt = channelResult.getAmtUnit() == Channel.AmtUnit.FEN ? (new BigDecimal(channelResult.getAmount()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)) : new BigDecimal(channelResult.getAmount());
                if (cashOutOrder.getOrderAmt().compareTo(callBackAmt) != 0) {
                    return false;//金额不一致
                }
                cashOutOrderService.handleCashOutOrder(cashOutOrder, channelResult);
                return true;
            }
        } catch (InterruptedException e) {
            log.error("获取更新用户缓存余额分布式锁失败：{}",e.getMessage());//暂不做处理
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
        throw new BusinessException(ResultCode.LOCK_NOT_RELEASED);
    }
}
