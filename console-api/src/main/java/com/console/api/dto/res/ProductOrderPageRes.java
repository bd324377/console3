package com.console.api.dto.res;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.console.core.model.res.IPageRes;
import com.console.product.entity.ProductOrder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductOrderPageRes extends IPageRes<ProductOrder> {
    private BigDecimal orderAmt;//订单金额
    private BigDecimal payoutAmt;//支出金额
    private BigDecimal resultAmt;//订单结果金额
    public ProductOrderPageRes(IPage<ProductOrder> productOrderIPage) {
        this.setSize(productOrderIPage.getSize());
        this.setCurrent(productOrderIPage.getCurrent());
        this.setTotal(productOrderIPage.getTotal());
        this.setPages(productOrderIPage.getPages());
        this.setRecords(productOrderIPage.getRecords());
    }
}
