package com.console.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@TableName("s_platform_belong_rule_item")
public class PlatformBelongRuleItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId
    private Integer id;
    private Integer ruleId;//规则ID
    private Integer platformId;//产商ID
    private String  platformName;//产商名称
    private String  merchantCode;//商户id
    private String  subMerchantCode;//子商户id
    private String  secretkey;//商户秘钥
    @TableField(exist = false)
    private Integer userSuffix;//用户后缀
}
