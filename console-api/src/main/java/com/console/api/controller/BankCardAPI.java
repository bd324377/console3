package com.console.api.controller;

import com.console.api.dto.req.BankCardReq;
import com.console.api.service.BankCardAPIService;
import com.console.framework.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BankCardAPI {
    @Resource
    private BankCardAPIService bankCardAPIService;

    @PostMapping("/bankCardList")
    public Result getBankCardInfo() {
        return Result.success(bankCardAPIService.getBankCardBankList());
    }

    /**
     * 添加提现手机号(添加银行卡)
     */
    @PostMapping("/addUserPhoneBankCard")
    public Result addUserPhoneBankCard(@RequestBody BankCardReq.AddBankCardReq addPhoneBankCardReq) {
        return bankCardAPIService.addUserPhoneBankCard(addPhoneBankCardReq) ? Result.success() : Result.failed(500);
    }

    /**
     * 添加提现邮箱(添加银行卡)
     */
    @PostMapping("/addUserEmailBankCard")
    public Result addUserEmailBankCard(@RequestBody BankCardReq.AddBankCardReq addEmailBankCardReq) {
        return bankCardAPIService.addUserEmailBankCard(addEmailBankCardReq) ? Result.success() : Result.failed(500);
    }

    /**
     * 添加提现银行卡
     */
    @PostMapping("/addUserBankCard")
    public Result addUserBankCard(@RequestBody BankCardReq.AddBankCardReq addBankCardReq) {
        return bankCardAPIService.addUserBankCard(addBankCardReq) ? Result.success() : Result.failed(500);
    }
}
