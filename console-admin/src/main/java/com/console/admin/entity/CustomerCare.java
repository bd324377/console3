package com.console.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@TableName(value = "s_customer_care")
public class CustomerCare implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;                     // 客服id
    private Integer marketSiteId;           // 站点id，0表租户默认客服
    private Integer marketTeamId;           // 业务渠道id，0表站点默认客服
    private Integer marketerId;             // 业务员ID，0表渠道默认客服
    private String customerCareName;        // 客服名称
    private String icon;                    // 客服图标
    private String routePath;               // 客服路由
    private String desc;                    // 客服描述
    private Integer sort;
}
