package com.console.api.dto.res;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.console.activity.entity.ActivityProc;
import com.console.core.model.res.IPageRes;
import com.console.payment.entity.CashOutOrder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReceiveRewardLogPageRes extends IPageRes<ActivityProc> {
    private BigDecimal receivedAmt;

    public ReceiveRewardLogPageRes(IPage<ActivityProc> activityProcIPage) {
        this.setSize(activityProcIPage.getSize());
        this.setCurrent(activityProcIPage.getCurrent());
        this.setTotal(activityProcIPage.getTotal());
        this.setPages(activityProcIPage.getPages());
        this.setRecords(activityProcIPage.getRecords());
    }
}
