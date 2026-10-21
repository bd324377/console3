package com.console.product.service.impl;

import com.console.core.service.impl.BaseServiceImpl;
import com.console.product.entity.ProductOrder;
import com.console.product.mapper.ProductOrderMapper;
import com.console.product.service.ProductOrderService;
import org.springframework.stereotype.Service;

@Service
public class ProductOrderServiceImpl extends BaseServiceImpl<ProductOrderMapper,ProductOrder> implements ProductOrderService {
}
