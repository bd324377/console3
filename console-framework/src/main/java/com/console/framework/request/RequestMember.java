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
    private Integer cashOutStatus;  //个人提现状态 1是0否
    private Integer loginChannel;
    private Integer rechargeTimes;//充值次数
    private LocalDateTime rechargeTime;//上次充值时间
    private String cashOutPwd;//提现密码
    private Integer userType;//用户类型
    private Integer inviteRewardStatus;//邀请奖励状态（是否能参与邀新活动：1、能；0不能）
    private Integer transferStatus = 0;//进入中转平台状态 0、未进入；1、已进入中转平台

    private String tokenKey;
}
