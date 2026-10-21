package com.console.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@TableName("s_platform_belong_rule")
public class PlatformBelongRule implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId
    private Integer id;
    private Integer platformBelongId;//归属id
    private String  ruleName;//规则名称
    private Integer userSuffix;//用户后缀
}
