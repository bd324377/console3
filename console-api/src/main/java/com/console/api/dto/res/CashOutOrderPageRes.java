package com.console.api.dto.res;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.console.core.model.res.IPageRes;
import com.console.payment.entity.CashOutOrder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CashOutOrderPageRes extends IPageRes<CashOutOrder> {
    private BigDecimal totalCashOutAmt;
    private BigDecimal totalPendingReviewAmt;

    public CashOutOrderPageRes(IPage<CashOutOrder> cashOutOrderIPage) {
        this.setSize(cashOutOrderIPage.getSize());
        this.setCurrent(cashOutOrderIPage.getCurrent());
        this.setTotal(cashOutOrderIPage.getTotal());
        this.setPages(cashOutOrderIPage.getPages());
        this.setRecords(cashOutOrderIPage.getRecords());
    }
}
