package com.console.product.service.impl;

import com.console.core.service.impl.BaseServiceImpl;
import com.console.product.entity.TypeProduct;
import com.console.product.mapper.TypeProductMapper;
import com.console.product.service.TypeProductService;
import org.springframework.stereotype.Service;

@Service
public class TypeProductServiceImpl extends BaseServiceImpl<TypeProductMapper, TypeProduct> implements TypeProductService {
}
