package com.console.api.dto.res;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.console.core.entity.WalletProc;
import com.console.core.model.res.IPageRes;
import com.console.payment.entity.WalletReport;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletLogPageRes extends IPageRes<WalletProc> {
    private WalletReport walletReport;
    public WalletLogPageRes(IPage<WalletProc> walletProcIPage) {
        this.setSize(walletProcIPage.getSize());
        this.setCurrent(walletProcIPage.getCurrent());
        this.setTotal(walletProcIPage.getTotal());
        this.setPages(walletProcIPage.getPages());
        this.setRecords(walletProcIPage.getRecords());
    }
}
