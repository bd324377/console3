package com.console.payment.channel.strategy;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.console.core.entity.UserProc;
import com.console.core.entity.Wallet;
import com.console.core.entity.WalletProc;
import com.console.core.merger.UserProcMerger;
import com.console.core.merger.WalletProcMerger;
import com.console.core.service.WalletService;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.constants.ActivityEvent;
import com.console.framework.constants.ActivityType;
import com.console.framework.constants.CacheConstants;
import com.console.framework.utils.DateUtils;
import com.console.framework.utils.SpringUtils;
import com.console.framework.utils.TenantUtils;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.entity.CashOutOrder;
import com.console.payment.entity.CashOutProc;
import com.console.payment.entity.PaymentOrder;
import com.console.payment.entity.PaymentProc;
import com.console.payment.merger.CashOutProcMerger;
import com.console.payment.merger.PaymentProcMerger;
import com.console.payment.service.CashOutOrderService;
import com.console.payment.service.PaymentOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Component
@Slf4j
public class OrderCallBackHandler {
    @Resource
    private PaymentOrderService paymentOrderService;
    @Resource
    private CashOutOrderService cashOutOrderService;
    @Resource
    private WalletService walletService;
    @Resource
    private UserProcMerger userProcMerger;
    @Resource
    private WalletProcMerger walletProcMerger;
    @Resource
    private PaymentProcMerger paymentProcMerger;
    @Resource
    private CashOutProcMerger cashOutProcMerger;
    @Resource
    private RedisLockRegistry redisLockRegistry;
    @Async
    public void handleCashOutOrderByCallBack(PaymentOrder paymentOrder, ChannelResult channelResult) {
        String lockKey = CacheConstants.ACQUIRE_LOCK_UPDATE_PAYMENT_ORDER_STATE + channelResult.getTenantId() + ":" + paymentOrder.getId();
        Lock lock = redisLockRegistry.obtain(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(100, TimeUnit.MILLISECONDS);
            if (locked) {
                if (paymentOrder.getStatus() != PaymentOrder.PaymentStatus.PAYING) return;
                DynamicTableNameHandler.setTenantId(channelResult.getTenantId());
                UpdateWrapper<PaymentOrder> orderUpdateWrapper = new UpdateWrapper<>();
                orderUpdateWrapper.lambda().eq(PaymentOrder::getId,paymentOrder.getId()).set(PaymentOrder::getCompleteStatus, 1);
                if (channelResult.isSuccess() && channelResult.getStatus() == 0) {
                    QueryWrapper<Wallet> queryWrapper = new QueryWrapper<>();
                    queryWrapper.select("id,balance,to_be_order_amt,recharge_times,recharge_amt")
                            .lambda().eq(Wallet::getId, paymentOrder.getUserId()).eq(Wallet::getWalletType, Wallet.WalletType.USER_WALLET);
                    Wallet wallet = walletService.getOne(queryWrapper);
                    UpdateWrapper<Wallet> walletUpdateWrapper = new UpdateWrapper<>();
                    walletUpdateWrapper.lambda().eq(Wallet::getUserId,paymentOrder.getUserId())
                            .eq(Wallet::getWalletType, Wallet.WalletType.USER_WALLET)
                            .set(Wallet::getLastRechargeTime, LocalDateTime.now());
                    walletUpdateWrapper.setSql("balance = balance + " + paymentOrder.getOrderAmt())
                            .setSql("recharge_amt = recharge_amt + " + paymentOrder.getOrderAmt())
                            .setSql("recharge_times = recharge_times + 1");
                    if (paymentOrder.getToBeOrderAmt() != null && paymentOrder.getToBeOrderAmt().compareTo(BigDecimal.ZERO) > 0) {
                        walletUpdateWrapper.setSql("to_be_order_amt = to_be_order_amt + " + paymentOrder.getToBeOrderAmt());
                    } else {
                        walletUpdateWrapper.setSql("cash_able_amt = cash_able_amt + " + paymentOrder.getOrderAmt());
                    }
                    orderUpdateWrapper.lambda()
                            .set(PaymentOrder::getStatus, PaymentOrder.PaymentStatus.SUCCESS.getKey())
                            .set(PaymentOrder::getPayTime, LocalDateTime.now())
                            .set(PaymentOrder::getRechargeTimes, wallet.getRechargeTimes() + 1)
                            .set(PaymentOrder::getOrderType, paymentOrder.getOrderType())
                            .set(StringUtils.hasText(paymentOrder.getRemarks()), PaymentOrder::getRemarks, paymentOrder.getRemarks())
                            .eq(PaymentOrder::getId, paymentOrder.getId());
                    if (walletService.update(walletUpdateWrapper) && paymentOrderService.update(orderUpdateWrapper)) {
                        //添加钱包过程
                        WalletProc walletProc = new WalletProc(paymentOrder.getId(), paymentOrder.getUserId(), Wallet.WalletType.USER_WALLET, 1,
                                WalletProc.ProcType.ONLINE_RECHARGE, wallet.getBalance().add(paymentOrder.getOrderAmt()),paymentOrder.getOrderAmt(),paymentOrder.getToBeOrderAmt());
                        //TODO 钱包金额更新，给前端推送更新信息
                        walletProcMerger.submit(walletProc);
                        //添加用户充值记录，如果是首充还要添加首充记录
                        UserProc rechargeProc = new UserProc(paymentOrder.getUserId(), TenantUtils.getTenantId(), UserProc.ProcType.RECHARGE);
                        userProcMerger.submit(rechargeProc);
                        if (wallet.getRechargeTimes() == 0) {//首充
                            UserProc firstRechargeProc = new UserProc(paymentOrder.getUserId(), TenantUtils.getTenantId(), UserProc.ProcType.FIRST_RECHARGE);
                            userProcMerger.submit(firstRechargeProc);
                        }
                        //添加支付成功过程
                        PaymentProc paymentProc = new PaymentProc(paymentOrder, PaymentProc.ProcType.ONLINE_PAYMENT_SUCCESS);
                        paymentProcMerger.submit(paymentProc);
                        if (paymentOrder.getActivityId() != null) {//参与单次充值活动//0、用户ID；1、充值次数；2、充值金额
                            String paramStr = paymentOrder.getUserId() + ";" + paymentOrder.getRechargeTimes() + ";" + paymentOrder.getOrderAmt();
                            ActivityEvent activityEvent = new ActivityEvent(paymentOrder.getActivityId(),
                                    ActivityType.SINGLE_RECHARGE,paymentOrder.getUserId(),channelResult.getTenantId(),paramStr);
                            SpringUtils.publishEvent(activityEvent);
                        }
                    }
                }
            } else {//失败则添加失败信息
                UpdateWrapper<PaymentOrder> updateWrapper = new UpdateWrapper<>();
                updateWrapper.lambda().eq(PaymentOrder::getId, paymentOrder.getId()).set(PaymentOrder::getErrorMsg, channelResult.getMessage());
                paymentOrderService.update(updateWrapper);
                //添加支付失败过程
                PaymentProc paymentProc = new PaymentProc(paymentOrder, PaymentProc.ProcType.ONLINE_PAYMENT_FAILED);
                paymentProcMerger.submit(paymentProc);
            }
        } catch (InterruptedException e) {
            log.error("支付订单回调，更新支付订单状态锁失败：{}",e.getMessage());//暂不做处理
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }

    @Async
    public void handleCashOutOrderByCallBack(CashOutOrder cashOutOrder, ChannelResult channelResult) {
        String lockKey = CacheConstants.ACQUIRE_LOCK_UPDATE_CASH_OUT_ORDER_STATE + channelResult.getTenantId() + ":" + cashOutOrder.getId();
        Lock lock = redisLockRegistry.obtain(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(100, TimeUnit.MILLISECONDS);
            if (locked) {
                if (cashOutOrder.getStatus() != CashOutOrder.CashOutStatus.CASHOUTING || cashOutOrder.getCompleteStatus() == 1) return;
                UpdateWrapper<CashOutOrder> orderUpdateWrapper = new UpdateWrapper<>();
                orderUpdateWrapper.lambda().eq(CashOutOrder::getId, cashOutOrder.getId()).set(CashOutOrder::getCompleteStatus, 1);

                UpdateWrapper<Wallet> walletUpdateWrapper = new UpdateWrapper<>();
                walletUpdateWrapper.lambda().eq(Wallet::getUserId, cashOutOrder.getUserId()).eq(Wallet::getWalletType,cashOutOrder.getWalletType());
                if (channelResult.getStatus() == 0) {//提现成功
                    orderUpdateWrapper.lambda()
                            .set(CashOutOrder::getStatus, CashOutOrder.CashOutStatus.SUCCESS)
                            .set(CashOutOrder::getCashOutTime, DateUtils.getCurrentTimestamp());
                    walletUpdateWrapper.lambda()
                            .setSql("cash_out_times = cash_out_times + 1")
                            .setSql("cash_out_amt = cash_out_amt + " + cashOutOrder.getOrderAmt());
                    if (cashOutOrderService.update(orderUpdateWrapper) && walletService.update(walletUpdateWrapper)) {
                        //添加用户提现过程记录
                        UserProc.ProcType procType = cashOutOrder.getWalletType() == Wallet.WalletType.USER_WALLET ? UserProc.ProcType.USER_CASH_OUT : UserProc.ProcType.AGENT_CASH_OUT;
                        UserProc userProc = new UserProc(cashOutOrder.getUserId(), channelResult.getTenantId(), procType);
                        userProcMerger.submit(userProc);
                        //添加提现成功记录
                        CashOutProc cashOutSuccessProc = new CashOutProc(cashOutOrder, CashOutProc.ProcType.ONLINE_CASH_OUT_SUCCESS);
                        cashOutProcMerger.submit(cashOutSuccessProc);
                    }
                } else if (channelResult.getStatus() == 3) {//提现失败
                    QueryWrapper<Wallet> queryWrapper = new QueryWrapper<>();
                    queryWrapper.select("balance").lambda().eq(Wallet::getUserId, cashOutOrder.getUserId()).eq(Wallet::getWalletType,cashOutOrder.getWalletType());
                    Wallet wallet = walletService.getOne(queryWrapper);
                    orderUpdateWrapper.lambda()
                            .set(CashOutOrder::getStatus, CashOutOrder.CashOutStatus.FAILED)
                            .set(CashOutOrder::getCashOutTime, DateUtils.getCurrentTimestamp());
                    //需要退款
                    walletUpdateWrapper.lambda()
                            .setSql("balance = balance + " + cashOutOrder.getOrderAmt())
                            .setSql("cash_able_amt = cash_able_amt + " + cashOutOrder.getOrderAmt());
                    if (cashOutOrderService.update(orderUpdateWrapper) && walletService.update(walletUpdateWrapper)) {
                        //钱包退款记录
                        WalletProc walletProc = new WalletProc(cashOutOrder.getId(), cashOutOrder.getUserId(), cashOutOrder.getWalletType(), 1, WalletProc.ProcType.CASH_OUT_FAILED_REFUND, wallet.getBalance().add(cashOutOrder.getOrderAmt()), cashOutOrder.getOrderAmt(),null);
                        walletProcMerger.submit(walletProc);
                        //提现失败记录
                        CashOutProc startCashOutProc = new CashOutProc(cashOutOrder, CashOutProc.ProcType.ONLINE_CASH_OUT_FAILED);
                        cashOutProcMerger.submit(startCashOutProc);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("提现订单回调，更新提现订单状态锁失败：{}",e.getMessage());//暂不做处理
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }
}
