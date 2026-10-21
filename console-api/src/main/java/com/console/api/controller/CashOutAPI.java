package com.console.api.controller;

import com.console.api.dto.CashOutDTO;
import com.console.api.service.CashOutAPIService;
import com.console.framework.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CashOutAPI {
    @Resource
    private CashOutAPIService cashOutAPIService;

    /**
     * 发起提现
     */
    @RequestMapping("/createCashOutOrder")
    public Result createCashOutOrder(@RequestBody @Validated CashOutDTO.CreateCashOutOrderReq cashOutOrderReq) {
        cashOutAPIService.createCashOutOrder(cashOutOrderReq);
        return Result.success();
    }

    /**
     * 分页查询提现订单
     */
    @PostMapping("/cashOutOrderRecord")
    public Result getCashOutOrderRecord(@RequestBody CashOutDTO.SearchCashOutOrderReq searchCashOutOrderReq) {
        return Result.success(cashOutAPIService.getCashOutOrderRecords(searchCashOutOrderReq));
    }
}
