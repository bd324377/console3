package com.console.api.controller;

import com.console.api.service.CustomerCareAPIService;
import com.console.framework.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerCareAPI {
    @Resource
    private CustomerCareAPIService customerCareAPIService;

    @PostMapping("/customerCares")
    public Result getCustomerCares() {
        return Result.success(customerCareAPIService.getCustomerCares());
    }
}
