package com.console.api.service;

import com.console.api.dto.ProductDTO;
import com.console.core.entity.Wallet;

public interface ProductAPIService {
    String enterProduct(ProductDTO.EnterProductRes enterProductRes);//进去产品中转服务
    Wallet logoutTransfer();//退出中转平台
}
