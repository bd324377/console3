package com.console.product.controller;

import com.console.framework.domain.Result;
import com.console.product.dto.ProductPlatformReq;
import com.console.product.service.ProductPlatformService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/productPlatform")
public class ProductPlatformController {
    @Resource
    private ProductPlatformService productPlatformService;

    /**
     * 从总租户同步产品列表
     */
    @PostMapping
    public Result syncPlatformList() {
        return productPlatformService.syncPlatformList() ? Result.success() : Result.failed();
    }

    @PutMapping("/{field}")
    public Result updateProductPlatform(@PathVariable String field, @RequestBody @Validated ProductPlatformReq.UpdateProductPlatformReq updateProductPlatformReq) {
        return productPlatformService.updateProductPlatform(field,updateProductPlatformReq) ? Result.success() : Result.failed();
    }
}
