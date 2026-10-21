package com.console.product.service;

public interface TransferService {
    void forceRefreshProductData();//强制不同中转平台产品数据
    void fetchProductData();//同步中转平台产品数据
    void fetchProductOrderData();//同步中转平台产品订单数据
}
