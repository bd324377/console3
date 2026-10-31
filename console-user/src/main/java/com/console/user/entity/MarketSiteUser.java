package com.console.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("s_market_site_user")
public class MarketSiteUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Integer id;//真实用户ID
    private Integer userId;//按位异或操作后的用户ID
    private Integer partitionCode;//分区记录 1~9
    private Integer tenantId;//租户id
    private Integer marketSiteId;//站点id
    private String  phone;
    private LocalDateTime bindTime;
    private BigDecimal rechargeAmt;
    private BigDecimal orderAmt;
    private BigDecimal orderResultAmt;

    @TableField(exist = false)
    private Integer retryTimes;
    @TableField(exist = false)
    private Boolean fromRecovery;
}
