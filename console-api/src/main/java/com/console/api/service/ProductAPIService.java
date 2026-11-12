package com.console.api.service;

import com.console.api.dto.req.ProductReq;
import com.console.api.dto.res.ProductOrderPageRes;
import com.console.core.entity.Wallet;

import java.util.List;

public interface ProductAPIService {
    String enterProduct(ProductReq.EnterProductReq enterProductReq);//进去产品中转服务
    Wallet logoutTransfer();//退出中转平台
    List<Integer> getCollectProductIds();
    void collectProduct(Integer productId);
    void cancelCollectProduct(Integer productId);
    List<Integer> getHistoryProductIds();
    ProductOrderPageRes getProductOrderRecord(ProductReq.SearchProductOrderReq searchProductOrderReq);
}
