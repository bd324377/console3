package com.console.product.controller;

import com.console.framework.domain.Result;
import com.console.product.model.req.OrderModeReq;
import com.console.product.service.OrderModeService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orderMode")
public class OrderModeController {
    @Resource
    private OrderModeService orderModeService;

    @PutMapping("/{field}")
    public Result updateOrderMode(@PathVariable String field, @RequestBody @Validated OrderModeReq.UpdateOrderModeReq updateOrderModeReq) {
        return orderModeService.updateOrderMode(field,updateOrderModeReq) ? Result.success() : Result.failed();
    }
}
