package com.console.product.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("s_product_platform")
public class ProductPlatform {
    private Integer id;
    private PlatformBelong.Belong belong;
    private Integer status;

}
