package com.console.payment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.entity.CashOutOrder;
import com.console.payment.mapper.CashOutOrderMapper;
import com.console.payment.model.CashOutStatus;
import com.console.payment.service.CashOutOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CashOutOrderServiceImpl extends ServiceImpl<CashOutOrderMapper, CashOutOrder> implements CashOutOrderService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleCashOutOrder(CashOutOrder order, ChannelResult result) {
        int status = mapStatus(result.getStatus());
        boolean complete = CashOutStatus.terminal(status);
        LocalDateTime cashOutTime = status == CashOutStatus.SUCCESS ? LocalDateTime.now() : order.getCashOutTime();
        String errorMessage = errorMessage(status, result.getMessage());

        // 数据库条件更新用于并发幂等，终态订单不能被后续通知回退。
        boolean changed = lambdaUpdate()
                .eq(CashOutOrder::getId, order.getId())
                .notIn(CashOutOrder::getStatus, CashOutStatus.REJECTED, CashOutStatus.SUCCESS,
                        CashOutStatus.FAILED, CashOutStatus.CANCELLED, CashOutStatus.REFUNDED,
                        CashOutStatus.TIMEOUT)
                .set(CashOutOrder::getStatus, status)
                .set(CashOutOrder::getCompleteState, complete ? 1 : 0)
                .set(CashOutOrder::getTransactionId, result.getOrderNo())
                .set(CashOutOrder::getCashOutTime, cashOutTime)
                .set(CashOutOrder::getErrorMsg, errorMessage)
                .update();

        if (changed) {
            order.setStatus(status);
            order.setCompleteState(complete ? 1 : 0);
            order.setTransactionId(result.getOrderNo());
            order.setErrorMsg(errorMessage);
            order.setCashOutTime(cashOutTime);
        }
        return changed;
    }

    private int mapStatus(String status) {
        if (status == null) {
            throw new IllegalArgumentException("cash-out callback status is missing");
        }
        return switch (status) {
            case "TOBE_AUDIT" -> CashOutStatus.PENDING;
            case "PAYING" -> CashOutStatus.PAYING;
            case "SUCCESS" -> CashOutStatus.SUCCESS;
            case "FAILED" -> CashOutStatus.FAILED;
            case "REJECT" -> CashOutStatus.REJECTED;
            default -> throw new IllegalArgumentException("unknown cash-out callback status: " + status);
        };
    }

    private String errorMessage(int status, String message) {
        return status == CashOutStatus.FAILED || status == CashOutStatus.REJECTED ? message : null;
    }
}
