package com.console.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.console.framework.constants.ValidationGroup;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@TableName(value = "tenant_domain")
public class TenantDomain implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(groups = ValidationGroup.update.class)
    @TableId(type = IdType.AUTO)
    private Integer id;             //站点域名关系表ID
    @NotNull(groups = ValidationGroup.insert.class)
    private Integer tenantId;       //租户ID
    @NotNull(groups = ValidationGroup.insert.class)
    private String  domain;         //域名
    private Integer status;         //域名使用状态：1-启用，0-停用
}
