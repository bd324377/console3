package com.console.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.EnumValueUtils;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName(value = "p_admin_proc")
public class AdminProc implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;                        //管理员日志ID
    private Integer backendUserId;          //后台用户ID
    private String backendUserName;         //后台用户名称
    private Integer backendUserTenantId;    //后台用户所属租户编码
    private Long objectId;                  //操作对象ID
    private String objectBeforeValue;       //操作前的对象值（JSON格式）
    private String objectValue;             //操作后的对象值（JSON格式）
    private Integer objectTenantId;         //操作对象租户编码
    private LocalDate procDate;             //操作日期
    private LocalDateTime procTime;         //操作时间
    private ProcBelong procBelong;          //操作行为归属
    private ProcType procType;              //操作类型
    private String ip;                      //IP地址
    private String os;                      //操作系统
    private String browser;                 //浏览器
    private String description;             //信息说明描述

    @TableField(exist = false)
    private Integer tenantId;
    @TableField(exist = false)
    private Integer retryTimes;
    @TableField(exist = false)
    private Boolean fromRecovery;

    public AdminProc() {}
    public AdminProc(RequestBackendUser backendUser,
                     ProcBelong procBelong,
                     ProcType procType,
                     Serializable objectId,
                     Object objectBeforeValue,
                     Object objectValue) {
        this.procBelong = procBelong;
        this.procType   = procType;
        this.backendUserId   = backendUser.getId();
        this.backendUserName = backendUser.getUserName();
        this.backendUserTenantId = backendUser.getBelongTenantId();
        this.objectTenantId = backendUser.getTenantId();
        this.objectId = objectId == null ? null : Long.valueOf(objectId.toString());
        this.procDate = LocalDate.now();
        this.procTime = LocalDateTime.now();

        this.tenantId = backendUser.getTenantId();
        if (objectValue != null) {
            if (objectValue instanceof Enum<?> e) {
                this.objectValue = EnumValueUtils.getEnumValue(e).toString();
            } else {
                this.objectValue = objectValue.toString();
            }
        }

        if (objectBeforeValue != null) {
            if (objectBeforeValue instanceof Enum<?> e) {
                this.objectBeforeValue = EnumValueUtils.getEnumValue(e).toString();
            } else {
                this.objectBeforeValue = objectBeforeValue.toString();
            }
        }
    }
    @Getter
    public enum ProcBelong {//过程归属
        SELE(0,"自身管理"),
        TENANT(1,"租户管理"),
        TENANT_DOMAIN(2,"租户域名配置"),
        MARKET_SITE(3,"站点管理"),
        MARKET_SITE_DOMAIN(4,"站点域名配置"),
        MARKET_TEAM(5,"团队管理"),
        MARKETER(6,"业务员管理"),
        USER(7,"用户管理"),
        PRODUCT_PLATFORM(8,"产商管理"),
        PRODUCT_ORDER_MODE(9,"下单模式"),
        MEDIA(10,"多媒体管理"),
        SHORTCUT_ICON(10,"多媒体管理"),
        PRODUCT_TYPE(11,"产品类型管理"),
        PRODUCT(12,"产品管理"),

        MENU(2,"菜单管理"),
        ROLE_MENU(3,"权限管理"),
        AGEMT_LEVEL(5,"代理等级管理"),
        PAYMENT(6,"支付管理"),
        BANK_CARD(7,"收款账户管理");

        @JsonValue
        @EnumValue
        private final Integer key;
        private final String value;

        ProcBelong(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    @Getter
    public enum ProcType {
        //过程类型
        LOGIN(0, "登录"),
        LOGOUT(1,"退出登录"),
        ADD(2, "新增"),
        UPDATE(3, "修改"),
        DELETE(4, "删除");

        @JsonValue
        @EnumValue
        private final Integer key;
        private final String value;

        ProcType(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }

}
