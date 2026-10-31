package com.console.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@TableName("s_product_order_report")
public class ProductOrderReport implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer reportDate;
    private Long userId;
    private Long productId;
    private Long platformId;
    private Product.ProductCategory category;
    private BigDecimal orderAmt;
    private BigDecimal payoutAmt;
    private BigDecimal resultAmt;
}
