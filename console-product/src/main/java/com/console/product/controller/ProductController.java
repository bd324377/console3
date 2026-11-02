package com.console.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.framework.domain.Result;
import com.console.product.dto.ProductReq;
import com.console.product.entity.Product;
import com.console.product.service.ProductService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Resource
    private ProductService productService;

    @GetMapping
    public Result getProductPage(Page<Product> page, ProductReq.SearchProductReq searchProductReq) {
        return Result.success(productService.getProductPage(page,searchProductReq));
    }

    @PutMapping("/{field}")
    public Result updateProduct(@PathVariable String field, @RequestBody @Validated ProductReq.UpdateProductReq updateProductReq) {
        return productService.updateProduct(field,updateProductReq) ? Result.success() : Result.failed();
    }
}
