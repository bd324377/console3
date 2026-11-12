package com.console.api.controller;

import com.console.api.dto.CashOutDTO;
import com.console.api.dto.req.WalletReq;
import com.console.api.service.WalletAPIService;
import com.console.framework.domain.Result;
import com.console.framework.utils.I18nUtil;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WalletAPI {
    @Resource
    private WalletAPIService walletAPIService;

    @PostMapping("/walletBalance")
    public Result getUserWalletAmt() {
        return Result.success(walletAPIService.getWalletBalance());
    }

    @PostMapping("/agentWalletBalance")
    public Result getAgentWalletBalance() {
        return Result.success(walletAPIService.getAgentWalletBalance());
    }

    @PostMapping("/setCashOutPwd")
    public Result setCashOutPwd(@RequestBody @Validated CashOutDTO.CashOutPwdReq cashOutPwdReq) {
        return walletAPIService.setCashOutPwd(cashOutPwdReq.getPassword()) ? Result.success() : Result.failed(500);
    }

    @PostMapping("/verifyCashOutPwd")
    public Result verifyCashOutPwd(@RequestBody CashOutDTO.CashOutPwdReq cashOutPwdReq) {
        return walletAPIService.verifyCashOutPwd(cashOutPwdReq.getPassword()) ? Result.success() : Result.failed(500, I18nUtil.getI8nMsg("wallet.error.verifyCashOutPwd.pwdError"));
    }

    /**
     * 交易流水（下单、存款，提款、赠送、人工出入款）
     */
    @PostMapping("/walletLog")
    public Result getWalletLogRecords(@RequestBody WalletReq.SearchWalletLogReq searchWalletLogReq) {
        return Result.success(walletAPIService.getWalletLog(searchWalletLogReq));
    }
}
