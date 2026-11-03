package com.console.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.admin.entity.CustomerCare;
import com.console.admin.model.req.CustomerCareReq;
import com.console.admin.service.CustomerCareService;
import com.console.framework.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customerCare")
public class CustomerCareController {
    @Resource
    private CustomerCareService customerCareService;

    @PostMapping
    public Result addCustomerCare(@RequestBody CustomerCareReq.AddCustomerCareReq addCustomerCareReq) {
        return customerCareService.addCustomerCare(addCustomerCareReq) ? Result.success() : Result.failed();
    }

    @DeleteMapping("/{customerCareId}")
    public Result delCustomerCare(@PathVariable Integer customerCareId) {
        return customerCareService.delCustomerCare(customerCareId) ? Result.success() : Result.failed();
    }

    @PutMapping("/{field}")
    public Result updateCustomerCare(@PathVariable String field, @RequestBody @Validated CustomerCareReq.UpdateCustomerCareReq updateCustomerCareReq) {
        return customerCareService.updateCustomerCare(field,updateCustomerCareReq) ? Result.success() : Result.failed();
    }

    @GetMapping
    public Result getCustomerCarePage(Page<CustomerCare> page) {
        return Result.success(customerCareService.getCustomerCarePage(page));
    }
}
