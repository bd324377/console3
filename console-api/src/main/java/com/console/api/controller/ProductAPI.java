package com.console.api.controller;

import com.console.api.dto.req.ProductReq;
import com.console.api.service.ProductAPIService;
import com.console.framework.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductAPI {
    @Resource
    private ProductAPIService productAPIService;

    /**
     * 进入产品中转服务
     * @param enterProductReq 产品信息
     */
    @PostMapping("/enterProduct")
    public Result enterProduct(@RequestBody @Validated ProductReq.EnterProductReq enterProductReq) {
        return Result.success(productAPIService.enterProduct(enterProductReq));
    }

    /** 退出产品中转服务 **/
    @PostMapping("/logoutTransfer")
    public Result logoutTransfer() {
        return Result.success(productAPIService.logoutTransfer());
    }

    /** 收藏产品 **/
    @PostMapping("/collectProduct")
    public Result collectProduct(@RequestBody @Validated ProductReq.CollectProductReq collectProductReq) {
        productAPIService.collectProduct(collectProductReq.getProductId());
        return Result.success();
    }

    /** 取消收藏 **/
    @PostMapping("/cancelCollectProduct")
    public Result cancelCollectProduct(@RequestBody @Validated ProductReq.CollectProductReq collectProductReq) {
        productAPIService.cancelCollectProduct(collectProductReq.getProductId());
        return Result.success();
    }

    /** 获取产品历史记录 **/
    @RequestMapping("/productHistoryRecord")
    public Result getHistoryProductIdList() {
        return Result.success(productAPIService.getHistoryProductIdList());
    }
}
