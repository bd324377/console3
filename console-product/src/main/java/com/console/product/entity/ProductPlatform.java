package com.console.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("s_product_platform")
public class ProductPlatform {
    @TableId
    private Integer id;
    private String icon;
    private Long officialId;
    private String platformName;
    private Integer status;
    private Integer sort;
    private BigDecimal serviceRate;
    private LocalDateTime addTime;
    private LocalDateTime updateTime;
}
