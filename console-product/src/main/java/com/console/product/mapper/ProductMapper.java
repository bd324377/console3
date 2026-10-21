package com.console.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.console.product.entity.Product;

import java.util.List;

public interface ProductMapper extends BaseMapper<Product> {
    void batchInsertOrUpdate(List<Product> productList);
}
