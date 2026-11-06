package com.console.api.dto.res;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.console.core.model.res.IPageRes;
import com.console.payment.entity.PaymentOrder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentOrderPageRes extends IPageRes<PaymentOrder> {
    private BigDecimal totalPaymentAmt;

    public PaymentOrderPageRes(IPage<PaymentOrder> paymentOrderIPage) {
        this.setSize(paymentOrderIPage.getSize());
        this.setCurrent(paymentOrderIPage.getCurrent());
        this.setTotal(paymentOrderIPage.getTotal());
        this.setPages(paymentOrderIPage.getPages());
        this.setRecords(paymentOrderIPage.getRecords());
    }
}
