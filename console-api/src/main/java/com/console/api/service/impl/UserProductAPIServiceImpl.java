package com.console.api.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.api.dto.ProductOrderDTO;
import com.console.api.service.UserProductAPIService;
import com.console.product.entity.ProductOrder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProductAPIServiceImpl extends BaseServiceImpl implements UserProductAPIService {

    @Override
    public void collectProduct(Long productId) {

    }

    @Override
    public boolean cancelCollectProduct(Long productId) {
        return false;
    }

    @Override
    public List<Long> getHistoryProductIdList() {
        return null;
    }

    @Override
    public IPage<ProductOrder> getProductOrderPage(Page<ProductOrder> page, ProductOrderDTO.SearchOrderReq searchOrderDTO) {
        return null;
    }
}
