package com.console.admin.entity;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.console.framework.request.RequestBackendUser;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@TableName(value = "s_backend_user")
public class BackendUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;                 //后台用户ID
    private String  account;            //登录账号
    private AccountType accountType;    //账号类型：0-后台管理用户，1-分销用户
    private String  userName;           //管理员名称
    private String  loginPwd;           //登录密码
    private String  loginIp;            //最后登录IP
    private BackendUserStatus status;   //用户状态：0-停用，1-启用
    private String  accessibleIps;      //可访问IP列表（JSON数组格式）
    private String  accessibleTenants;  //可访问租户列表（JSON数组格式）
    private RoleType  role;            //角色ID列表
    private LocalDateTime loginTime;    //最后登录时间
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//更新时间
    private Integer createBy;//创建人
    private Integer updateBy;//更新人

    public List<String> getAccessibleIps() {
        if (StringUtils.hasText(this.accessibleIps)) {
            return JSON.parseArray(this.accessibleIps,String.class);
        }
        return null;
    }

    public List<Integer> getAccessibleTenants() {
        if (StringUtils.hasText(this.accessibleTenants)) {
            return JSON.parseArray(this.accessibleTenants,Integer.class);
        }
        return null;
    }

//    public List<Integer> getRoles() {
//        if (StringUtils.hasText(this.roles)) {
//            return JSON.parseArray(this.roles,Integer.class);
//        }
//        return null;
//    }

    public RequestBackendUser buildRequestUser(Integer belongTenantId,Integer targetTenantId) {
        RequestBackendUser requestBackendUser = new RequestBackendUser();
        requestBackendUser.setId(this.getId());
        requestBackendUser.setUserName(this.getUserName());
        requestBackendUser.setTenantId(targetTenantId);
        requestBackendUser.setBelongTenantId(belongTenantId);
        requestBackendUser.setRole(this.getRole().getValue());
        return requestBackendUser;
    }

    @Getter
    public enum AccountType {
        UNENABLE(0, "平台管理用户"),
        ENABLE(1, "分销用户");

        @JsonValue
        @EnumValue
        private final Integer value;
        private final String desc;

        AccountType(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }
    }

    @Getter
    public enum BackendUserStatus {
        UNENABLE(0, "停用"),
        ENABLE(1, "启用");

        @JsonValue
        @EnumValue
        private final Integer value;
        private final String desc;

        BackendUserStatus(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }
    }

    @Getter
    public enum RoleType {
        SUPER_ADMIN(1,"超级管理员"),
        TENANT_ADMIN(2,"租户管理员"),
        SITE_MARKETER(3,"站点业务员"),
        TRAFFIC_MARKETER(4,"流量业务员"),
        CUSTOMER_CARE(5,"客服"),
        AGENT_USER(6,"代理用户");

        @JsonValue
        @EnumValue
        private final Integer value;
        private final String desc;

        RoleType(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }
    }
}
