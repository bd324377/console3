package com.console.product.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProductOrder {
    private Integer userId;
    private LocalDateTime orderTime;//下单时间
    private LocalDateTime orderEndTime;//结单时间
    private String  userSuffix;//用户后缀
}
