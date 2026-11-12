package com.console.api.dto.res;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.console.core.model.res.IPageRes;
import com.console.product.entity.ProductOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductOrderPageRes extends IPageRes<ProductOrder> {

    public ProductOrderPageRes(IPage<ProductOrder> productOrderIPage) {
        this.setSize(productOrderIPage.getSize());
        this.setCurrent(productOrderIPage.getCurrent());
        this.setTotal(productOrderIPage.getTotal());
        this.setPages(productOrderIPage.getPages());
        this.setRecords(productOrderIPage.getRecords());
    }
}
