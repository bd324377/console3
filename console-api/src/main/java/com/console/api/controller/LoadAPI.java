package com.console.api.controller;

import com.console.api.service.LoadAPIService;
import com.console.framework.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoadAPI {
    @Resource
    private LoadAPIService loadAPIService;

    /** 加载产品信息 **/
    @PostMapping("/loadProductInfo")
    public Result loadProductInfo() {
        return Result.success(loadAPIService.getProductInfo());
    }

    /** 加载布局信息 **/
    @PostMapping("/loadLayout")
    public Result loadLayout() {
        return Result.success(loadAPIService.getProductInfo());
    }
}
