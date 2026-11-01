package com.console.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.product.entity.UserProduct;

import java.util.List;

public interface UserProductService extends IService<UserProduct> {
    List<Long> getHistoryProductIdList();
}
