package com.console.payment.service.impl;

import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.request.RequestMember;
import com.console.framework.utils.RequestUtils;
import com.console.framework.utils.TenantUtils;
import com.console.payment.channel.PaymentFactory;
import com.console.payment.channel.PaymentStrategy;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.channel.strategy.u2c.dto.res.BalanceRes;
import com.console.payment.entity.BankCard;
import com.console.payment.entity.CashOutOrder;
import com.console.payment.entity.PaymentChannel;
import com.console.payment.entity.PaymentOrder;
import com.console.payment.model.CashOutStatus;
import com.console.payment.model.OrderNumber;
import com.console.payment.model.PaymentStatus;
import com.console.payment.model.req.CreateCashOutReq;
import com.console.payment.model.req.CreatePaymentReq;
import com.console.payment.service.BankCardService;
import com.console.payment.service.CashOutOrderService;
import com.console.payment.service.PaymentApplicationService;
import com.console.payment.service.PaymentChannelService;
import com.console.payment.service.PaymentOrderService;
import com.console.payment.utils.PaymentUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * 支付业务编排服务：管理本地订单生命周期，并把渠道协议细节委托给 PaymentStrategy。
 */
@Service
@RequiredArgsConstructor
public class PaymentApplicationServiceImpl implements PaymentApplicationService {
    private final PaymentOrderService paymentOrderService;
    private final CashOutOrderService cashOutOrderService;
    private final PaymentChannelService paymentChannelService;
    private final BankCardService bankCardService;
    private final PaymentFactory paymentFactory;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentOrder createPayment(CreatePaymentReq request, String clientIp) {
        PaymentChannel channel = requireChannel(request.getChannelId());
        validateAmount(request.getAmount(), channel.getMinPayAmt(), channel.getMaxPayAmt());
        PaymentStrategy strategy = paymentFactory.getStrategy(channel, false);

        PaymentOrder order = new PaymentOrder();
        order.setOrderType(1);
        order.setUserId(request.getUserId());
        applyMemberContext(order, request.getUserId());
        order.setPaymentChannelId(channel.getId());
        order.setStatus(PaymentStatus.PENDING);
        order.setOrderAmt(request.getAmount());
        order.setToBeOrderAmt(request.getAmount());
        order.setFee(calculateFee(request.getAmount(), channel.getPayFeeRate()));
        order.setOrderTime(LocalDateTime.now());
        order.setIp(clientIp);
        order.setAdvertId(request.getAdvertId());
        order.setRechargeTimes(1);
        order.setCompleteState(0);
        // 必须先落库取得自增 ID，才能生成全局可追踪的商户订单号。
        paymentOrderService.save(order);
        order.setMerchantOrderNo(OrderNumber.payment(TenantUtils.getTenantId(), order.getId()));
        paymentOrderService.updateById(order);

        // serviceCode 与本地订单号使用同一规则，最终生成 PAY_{tenantId}_{orderId}。
        String serviceCode = "PAY_" + TenantUtils.getTenantId();
        applyPaymentResult(order, strategy.createPayment(order, channel, serviceCode));
        return order;
    }

    @Override
    public PaymentOrder queryPayment(String merchantOrderNo) {
        PaymentOrder order = requirePayment(merchantOrderNo);
        if (!PaymentStatus.terminal(order.getStatus())) {
            PaymentChannel channel = requireChannel(order.getPaymentChannelId());
            applyPaymentResult(order, paymentFactory.getStrategy(channel, false).queryPayment(order, channel));
        }
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CashOutOrder createCashOut(CreateCashOutReq request) {
        PaymentChannel channel = requireChannel(request.getChannelId());
        validateAmount(request.getAmount(), channel.getMinCashOutAmt(), channel.getMaxCashOutAmt());
        PaymentStrategy strategy = paymentFactory.getStrategy(channel, true);

        BankCard bankCard = buildBankCard(request, channel.getCurType());
        bankCardService.save(bankCard);
        CashOutOrder order = new CashOutOrder();
        order.setOrderType(1);
        order.setWalletType(1);
        order.setBankCardCode(bankCard.getCardCode());
        order.setUserId(request.getUserId());
        applyMemberContext(order, request.getUserId());
        order.setPaymentChannelId(channel.getId());
        order.setStatus(CashOutStatus.PENDING);
        order.setOrderAmt(request.getAmount());
        order.setFee(calculateFee(request.getAmount(), channel.getCashOutFeeRate()));
        order.setOrderTime(LocalDateTime.now());
        order.setCashOutCount(1);
        order.setCompleteState(0);
        cashOutOrderService.save(order);
        order.setMerchantOrderNo(OrderNumber.cashOut(TenantUtils.getTenantId(), order.getId()));
        cashOutOrderService.updateById(order);
        try {
            applyCashOutResult(order, strategy.createCashOut(order, channel, bankCard));
        } catch (RuntimeException ex) {
            order.setErrorMsg(ex.getMessage());
            cashOutOrderService.updateById(order);
            throw ex;
        }
        return order;
    }

    @Override
    public CashOutOrder queryCashOut(String merchantOrderNo) {
        CashOutOrder order = requireCashOut(merchantOrderNo);
        if (!CashOutStatus.terminal(order.getStatus())) {
            PaymentChannel channel = requireChannel(order.getPaymentChannelId());
            applyCashOutResult(order, paymentFactory.getStrategy(channel, true).queryCashOut(order, channel));
        }
        return order;
    }

    @Override
    public List<BalanceRes> queryBalance(Long channelId) {
        PaymentChannel channel = requireChannel(channelId);
        return paymentFactory.getStrategy(channel, false).queryBalance(channel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentCallback(Map<String, Object> payload, String clientIp) {
        // 匿名回调没有登录租户上下文，需要先从商户订单号恢复租户表路由。
        OrderNumber number = setCallbackTenant(payload, "PAY");
        try {
            PaymentOrder order = requirePayment(String.valueOf(payload.get("merchantOrderNo")));
            PaymentChannel channel = requireChannel(order.getPaymentChannelId());
            PaymentStrategy strategy = paymentFactory.getStrategy(channel, false);
            verifyCallback(payload, clientIp, channel, strategy, order.getOrderAmt(), order.getMerchantOrderNo());
            ChannelResult result = strategy.parsePaymentCallback(payload, channel);
            verifyResultIdentity(result, channel, order.getMerchantOrderNo());
            if (!PaymentStatus.terminal(order.getStatus())) {
                boolean changed = applyPaymentCallbackResult(order, result);
                if (changed && order.getStatus() == PaymentStatus.SUCCESS) {
                    onPaymentSucceeded(order);
                }
            }
        } finally {
            DynamicTableNameHandler.removeTenantId();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleCashOutCallback(Map<String, Object> payload, String clientIp) {
        setCallbackTenant(payload, "CASH");
        try {
            CashOutOrder order = requireCashOut(String.valueOf(payload.get("merchantOrderNo")));
            PaymentChannel channel = requireChannel(order.getPaymentChannelId());
            PaymentStrategy strategy = paymentFactory.getStrategy(channel, true);
            verifyCallback(payload, clientIp, channel, strategy, order.getOrderAmt(), order.getMerchantOrderNo());
            ChannelResult result = strategy.parseCashOutCallback(payload, channel);
            verifyResultIdentity(result, channel, order.getMerchantOrderNo());
            if (!CashOutStatus.terminal(order.getStatus())) {
                boolean changed = applyCashOutCallbackResult(order, result);
                if (changed && order.getStatus() == CashOutStatus.SUCCESS) {
                    onCashOutSucceeded(order);
                }
            }
        } finally {
            DynamicTableNameHandler.removeTenantId();
        }
    }

    private OrderNumber setCallbackTenant(Map<String, Object> payload, String expectedType) {
        OrderNumber number = OrderNumber.parse(String.valueOf(payload.get("merchantOrderNo")));
        if (!expectedType.equals(number.type())) {
            throw new IllegalArgumentException("callback order type mismatch");
        }
        DynamicTableNameHandler.setTenantId(number.tenantId());
        return number;
    }

    private void verifyCallback(Map<String, Object> payload, String clientIp, PaymentChannel channel,
                                PaymentStrategy strategy, BigDecimal amount, String merchantOrderNo) {
        // 白名单为空表示只验签；配置白名单后必须同时满足来源 IP 校验。
        if (channel.getWhiteIps() != null && !channel.getWhiteIps().isEmpty()
                && (clientIp == null || !channel.getWhiteIps().contains(clientIp))) {
            throw new IllegalArgumentException("callback IP is not allowed");
        }
        if (!strategy.verifyCallback(payload, channel)) {
            throw new IllegalArgumentException("invalid callback signature");
        }
        Object callbackAmount = payload.get("paidAmount") == null ? payload.get("amount") : payload.get("paidAmount");
        if (callbackAmount == null || PaymentUtils.toCents(amount) != Integer.parseInt(String.valueOf(callbackAmount))) {
            throw new IllegalArgumentException("callback amount mismatch");
        }
        if (!merchantOrderNo.equals(String.valueOf(payload.get("merchantOrderNo")))) {
            throw new IllegalArgumentException("callback order mismatch");
        }
        if (!channel.getCurType().equalsIgnoreCase(String.valueOf(payload.get("currency")))) {
            throw new IllegalArgumentException("callback currency mismatch");
        }
        if (!channel.getAppKey().equals(String.valueOf(payload.get("merchantId")))) {
            throw new IllegalArgumentException("callback merchant mismatch");
        }
    }

    private void verifyResultIdentity(ChannelResult result, PaymentChannel channel, String merchantOrderNo) {
        if (!merchantOrderNo.equals(result.getMerchantOrderNo()) || !channel.getAppKey().equals(result.getMerchantId())) {
            throw new IllegalArgumentException("channel result identity mismatch");
        }
    }

    private void applyPaymentResult(PaymentOrder order, ChannelResult result) {
        if (result == null) {
            throw new IllegalStateException("empty channel result");
        }
        if (!result.isSuccess()) {
            // 远程失败不改变订单状态：创建请求可能已被渠道受理，查询失败也只是本次未能确认。
            order.setErrorMsg(channelError(result));
            paymentOrderService.updateById(order);
            return;
        }
        order.setTransactionId(result.getOrderNo());
        order.setPayUrl(result.getPayUrl());
        order.setStatus(mapPaymentStatus(result.getStatus()));
        order.setCompleteState(PaymentStatus.terminal(order.getStatus()) ? 1 : 0);
        if (order.getStatus() == PaymentStatus.SUCCESS) {
            order.setPayTime(LocalDateTime.now());
        }
        order.setErrorMsg(result.getMessage());
        paymentOrderService.updateById(order);
    }

    private void applyCashOutResult(CashOutOrder order, ChannelResult result) {
        if (result == null) {
            throw new IllegalStateException("empty channel result");
        }
        if (!result.isSuccess()) {
            // 提现创建超时同样存在“渠道已受理但响应丢失”的可能，必须等待查询确认。
            order.setErrorMsg(channelError(result));
            cashOutOrderService.updateById(order);
            return;
        }
        order.setTransactionId(result.getOrderNo());
        order.setStatus(mapCashOutStatus(result.getStatus()));
        order.setCompleteState(CashOutStatus.terminal(order.getStatus()) ? 1 : 0);
        if (order.getStatus() == CashOutStatus.SUCCESS) {
            order.setCashOutTime(LocalDateTime.now());
        }
        order.setErrorMsg(result.getMessage());
        cashOutOrderService.updateById(order);
    }

    private boolean applyPaymentCallbackResult(PaymentOrder order, ChannelResult result) {
        int status = mapPaymentStatus(result.getStatus());
        boolean complete = PaymentStatus.terminal(status);
        // 条件更新保证重复/乱序通知无法覆盖成功、失败、取消或退款等终态。
        boolean changed = paymentOrderService.lambdaUpdate()
                .eq(PaymentOrder::getId, order.getId())
                .notIn(PaymentOrder::getStatus, PaymentStatus.SUCCESS, PaymentStatus.FAILED,
                        PaymentStatus.CANCELLED, PaymentStatus.REFUNDED)
                .set(PaymentOrder::getStatus, status)
                .set(PaymentOrder::getCompleteState, complete ? 1 : 0)
                .set(PaymentOrder::getTransactionId, result.getOrderNo())
                .set(PaymentOrder::getPayTime, status == PaymentStatus.SUCCESS ? LocalDateTime.now() : order.getPayTime())
                .set(PaymentOrder::getErrorMsg, result.getMessage())
                .update();
        order.setStatus(status);
        order.setCompleteState(complete ? 1 : 0);
        order.setTransactionId(result.getOrderNo());
        return changed;
    }

    private boolean applyCashOutCallbackResult(CashOutOrder order, ChannelResult result) {
        int status = mapCashOutStatus(result.getStatus());
        boolean complete = CashOutStatus.terminal(status);
        // 提现终态同样不可被后续 PAYING 等旧通知回退。
        boolean changed = cashOutOrderService.lambdaUpdate()
                .eq(CashOutOrder::getId, order.getId())
                .notIn(CashOutOrder::getStatus, CashOutStatus.REJECTED, CashOutStatus.SUCCESS,
                        CashOutStatus.FAILED, CashOutStatus.CANCELLED, CashOutStatus.REFUNDED, CashOutStatus.TIMEOUT)
                .set(CashOutOrder::getStatus, status)
                .set(CashOutOrder::getCompleteState, complete ? 1 : 0)
                .set(CashOutOrder::getTransactionId, result.getOrderNo())
                .set(CashOutOrder::getCashOutTime, status == CashOutStatus.SUCCESS ? LocalDateTime.now() : order.getCashOutTime())
                .set(CashOutOrder::getErrorMsg, result.getMessage())
                .update();
        order.setStatus(status);
        order.setCompleteState(complete ? 1 : 0);
        order.setTransactionId(result.getOrderNo());
        return changed;
    }

    private int mapPaymentStatus(String status) {
        return switch (Objects.requireNonNull(status, "payment status is missing")) {
            case "WAITING_PAY", "PAYING" -> PaymentStatus.PAYING;
            case "PAID" -> PaymentStatus.SUCCESS;
            case "PAY_FAILED" -> PaymentStatus.FAILED;
            case "REFUND" -> PaymentStatus.REFUNDED;
            default -> throw new IllegalArgumentException("unknown U2C payment status: " + status);
        };
    }

    private int mapCashOutStatus(String status) {
        return switch (Objects.requireNonNull(status, "cash-out status is missing")) {
            case "TOBE_AUDIT" -> CashOutStatus.PENDING;
            case "PAYING" -> CashOutStatus.PAYING;
            case "SUCCESS" -> CashOutStatus.SUCCESS;
            case "FAILED" -> CashOutStatus.FAILED;
            case "REJECT" -> CashOutStatus.REJECTED;
            default -> throw new IllegalArgumentException("unknown U2C cash-out status: " + status);
        };
    }

    private PaymentChannel requireChannel(Long id) {
        PaymentChannel channel = paymentChannelService.getById(id);
        if (channel == null) {
            throw new IllegalArgumentException("payment channel does not exist");
        }
        return channel;
    }

    private PaymentOrder requirePayment(String merchantOrderNo) {
        PaymentOrder order = paymentOrderService.lambdaQuery().eq(PaymentOrder::getMerchantOrderNo, merchantOrderNo).one();
        if (order == null) {
            throw new IllegalArgumentException("payment order does not exist");
        }
        return order;
    }

    private CashOutOrder requireCashOut(String merchantOrderNo) {
        CashOutOrder order = cashOutOrderService.lambdaQuery().eq(CashOutOrder::getMerchantOrderNo, merchantOrderNo).one();
        if (order == null) {
            throw new IllegalArgumentException("cash-out order does not exist");
        }
        return order;
    }

    private BankCard buildBankCard(CreateCashOutReq request, String currency) {
        BankCard value = new BankCard();
        value.setCardCode(UUID.randomUUID().toString().replace("-", ""));
        value.setUserId(request.getUserId());
        value.setCurrency(currency);
        value.setAccountType(request.getAccountType());
        value.setBankCode(request.getBankCode());
        value.setBranchBankNo(request.getBranchBankNo());
        value.setBranchBankName(request.getBranchBankName());
        value.setAccountNo(request.getAccountNo());
        value.setAccountName(request.getAccountName());
        value.setAccountMobile(request.getAccountMobile());
        value.setAccountEmail(request.getAccountEmail());
        value.setProvince(request.getProvince());
        value.setCity(request.getCity());
        value.setCpf(request.getCpf());
        value.setIfsc(request.getIfsc());
        value.setStatus(1);
        value.setCreateTime(LocalDateTime.now());
        return value;
    }

    private void validateAmount(BigDecimal amount, BigDecimal minimum, BigDecimal maximum) {
        PaymentUtils.toCents(amount);
        if (minimum != null && amount.compareTo(minimum) < 0) {
            throw new IllegalArgumentException("amount is below channel minimum");
        }
        if (maximum != null && amount.compareTo(maximum) > 0) {
            throw new IllegalArgumentException("amount exceeds channel maximum");
        }
    }

    private BigDecimal calculateFee(BigDecimal amount, BigDecimal rate) {
        return rate == null ? BigDecimal.ZERO : amount.multiply(rate).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private String channelError(ChannelResult result) {
        String code = result.getErrorCode() == null ? "CHANNEL_ERROR" : result.getErrorCode();
        return code + ": " + (result.getMessage() == null ? "unknown channel error" : result.getMessage());
    }

    private void applyMemberContext(PaymentOrder order, Integer userId) {
        RequestMember member = RequestUtils.getRequestMember();
        if (member == null) {
            return;
        }
        if (!Objects.equals(member.getId(), userId)) {
            throw new IllegalArgumentException("cannot create a payment order for another user");
        }
        order.setSiteId(member.getSiteId());
        order.setTeamId(member.getTeamId());
        order.setMarketerId(member.getMarketerId());
    }

    private void applyMemberContext(CashOutOrder order, Integer userId) {
        RequestMember member = RequestUtils.getRequestMember();
        if (member == null) {
            return;
        }
        if (!Objects.equals(member.getId(), userId)) {
            throw new IllegalArgumentException("cannot create a cash-out order for another user");
        }
        order.setMarketSiteId(member.getSiteId());
        order.setMarketTeamId(member.getTeamId());
        order.setMarketerId(member.getMarketerId());
    }
}
