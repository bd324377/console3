package com.console.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.framework.domain.Result;
import com.console.product.dto.ProductTypeReq;
import com.console.product.entity.ProductType;
import com.console.product.service.ProductTypeService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/productType")
public class ProductTypeController {
    @Resource
    private ProductTypeService productTypeService;

    @PostMapping
    public Result addProductType(@RequestBody @Validated ProductTypeReq.AddProductTypeReq addProductTypeReq) {
        return productTypeService.addProductType(addProductTypeReq) ? Result.success() : Result.failed();
    }

    @DeleteMapping
    public Result delProductType(@RequestBody @Validated ProductTypeReq.DelProductTypeReq delProductTypeReq) {
        return productTypeService.delProductType(delProductTypeReq) ? Result.success() : Result.failed();
    }

    @PutMapping("/{field}")
    public Result updateProductType(@PathVariable String field,@RequestBody @Validated ProductTypeReq.UpdateProductTypeReq updateProductTypeReq) {
        return productTypeService.updateProductType(field,updateProductTypeReq) ? Result.success() : Result.failed();
    }

    @GetMapping
    public Result getProductTypePage(Page<ProductType> page, ProductTypeReq.SearchProductTypeReq searchProductTypeReq) {
        return Result.success(productTypeService.getProductTypePage(page,searchProductTypeReq));
    }
}
