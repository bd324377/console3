package com.console.payment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.entity.PaymentOrder;
import com.console.payment.mapper.PaymentOrderMapper;
import com.console.payment.model.PaymentStatus;
import com.console.payment.service.PaymentOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentOrderServiceImpl extends ServiceImpl<PaymentOrderMapper, PaymentOrder> implements PaymentOrderService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handlePaymentOrder(PaymentOrder order, ChannelResult result) {
        int status = mapStatus(result.getStatus());
        boolean complete = PaymentStatus.terminal(status);
        LocalDateTime payTime = status == PaymentStatus.SUCCESS ? LocalDateTime.now() : order.getPayTime();
        String errorMessage = errorMessage(status, result.getMessage());

        // 仅允许非终态订单更新，保证重复或乱序回调不会覆盖最终结果。
        boolean changed = lambdaUpdate()
                .eq(PaymentOrder::getId, order.getId())
                .notIn(PaymentOrder::getStatus, PaymentStatus.SUCCESS, PaymentStatus.FAILED,
                        PaymentStatus.CANCELLED, PaymentStatus.REFUNDED)
                .set(PaymentOrder::getStatus, status)
                .set(PaymentOrder::getCompleteState, complete ? 1 : 0)
                .set(PaymentOrder::getTransactionId, result.getOrderNo())
                .set(PaymentOrder::getPayTime, payTime)
                .set(PaymentOrder::getErrorMsg, errorMessage)
                .update();

        if (changed) {
            order.setStatus(status);
            order.setCompleteState(complete ? 1 : 0);
            order.setTransactionId(result.getOrderNo());
            order.setErrorMsg(errorMessage);
            order.setPayTime(payTime);
        }
        return changed;
    }

    private int mapStatus(String status) {
        if (status == null) {
            throw new IllegalArgumentException("payment callback status is missing");
        }
        return switch (status) {
            case "WAITING_PAY", "PAYING" -> PaymentStatus.PAYING;
            case "PAID" -> PaymentStatus.SUCCESS;
            case "PAY_FAILED" -> PaymentStatus.FAILED;
            case "REFUND" -> PaymentStatus.REFUNDED;
            default -> throw new IllegalArgumentException("unknown payment callback status: " + status);
        };
    }

    private String errorMessage(int status, String message) {
        return status == PaymentStatus.FAILED || status == PaymentStatus.REFUNDED ? message : null;
    }
}
