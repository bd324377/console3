package com.console.api.controller;

import com.console.api.dto.ProductDTO;
import com.console.api.service.ProductAPIService;
import com.console.framework.domain.Result;
import com.console.framework.utils.RequestUtils;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductAPI {
    @Resource
    private ProductAPIService productAPIService;

    /**
     * 进入产品中转服务
     * @param enterProductRes 产品信息
     */
    @PostMapping
    public Result enterProduct(@RequestBody @Validated ProductDTO.EnterProductRes enterProductRes) {
        return Result.success();
    }

    /**
     * 退出产品中转服务
     */
    @PostMapping("/logoutTransfer")
    public Result logoutTransfer() {
        return Result.success();
//        return Result.success(productAPIService.logoutTransfer(RequestUtils.getUser().getId(),false));
    }
}
