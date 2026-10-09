package com.console.framework.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RequestMember extends RequestUser {
    private Integer siteId;         //站点ID
    private Integer teamId;         //团队ID
    private Integer marketerId;     //业务员ID
    private Integer agentId;
    private Integer vipId;
    private Integer vipLevel;
    private String  phone;          //手机号码
    private Integer phoneStatus;     //手机板顶状态 1、绑定；0、未绑定
    private String  email;          //邮箱地址
    private Integer emailStatus;     //邮箱绑定状态 1、绑定；0、未绑定
    private Integer loginChannel;
    private Integer rechargeTimes;//充值次数
    private LocalDateTime rechargeTime;//上次充值时间
}
