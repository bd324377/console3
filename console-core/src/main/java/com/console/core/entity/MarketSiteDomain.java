package com.console.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@TableName(value = "r_market_site_domain")
public class MarketSiteDomain implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;             //站点域名关系表ID
    private Integer tenantId;       //租户ID
    private Integer marketSiteId;   //站点ID
    private String  siteDomain;     //域名
    private Integer status;         //域名使用状态：1-启用，0-停用
}
