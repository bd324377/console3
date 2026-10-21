package com.console.user.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.console.framework.constants.LoginChannel;
import com.console.framework.request.RequestMember;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("s_user")
public class User implements Serializable {//新增渠道和业务员参数

    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "id")
    private Integer id;
    private Integer parentId;//上级用户ID
    private Integer realParentId;//真实上级用户ID
    private Integer tenantId;//租户ID
    private Integer marketSiteId;//站点ID
    private Integer marketTeamId;//团队（渠道）ID
    private Integer marketerId;//业务员Id
    private UserType userType;//用户类型

    private String account;//登录账号
    private String loginPwd;//登录密码
    private String avatar;  //头像
    private String nickName;//昵称

    private Integer inviteRewardStatus;//邀请奖励状态（是否能参与邀新活动：1、能；0不能）
    private Integer activateStatus;//激活状态；0、未激活；1、已激活；
    private Integer activateTime;//激活时间

    private String phone;//电话号码
    private Integer phoneStatus;//手机状态：1、已绑定（接收过验证码，不可再变更）；0、未绑定，可变更
    private String email;//邮箱账号
    private Integer emailStatus;//邮箱状态：1、已绑定（接收过验证码，不可再变更）；0、未绑定，可变更
    private Integer status;//账号状态：1、正常；2、冻结；
    private Integer cashOutStatus;  //个人提现状态
    private Integer vipId;//VIP等级id
    private Integer agentId;//代理等级id
    private String regIp;//注册IP
    private LocalDateTime regTime;//注册时间
    private String regDeviceId;//注册设备ID
    private String loginIp;//本次登录ip
    private LocalDateTime loginTime;
    private Integer regChannel;//注册渠道
    private LoginChannel loginChannel;//登录渠道
    private String cashOutPwd;//提现密码
    private LocalDateTime chgVipTime;//改变vip等级时间
    private LocalDateTime chgAgentTime;//变更代理等级时间
    private String remarks;//备注

    @Getter
    public enum UserType {
        NORMAL(0,"正常用户"),
        TEST(1,"测试用户");

        @EnumValue
        @JsonValue
        private final Integer type;
        private final String  desc;

        UserType(int type, String desc) {
            this.type = type;
            this.desc = desc;
        }

        public static UserType getUserType(int key) {
            for (UserType type : values()) {
                if (type.getType() == key) {
                    return type;
                }
            }
            return null;
        }
    }

    public RequestMember buildRequestUser() {
        RequestMember requestMember = new RequestMember();
        requestMember.setId(this.getId());
        requestMember.setAccountType(1);
        requestMember.setTenantId(this.getTenantId());
        requestMember.setSiteId(this.getMarketSiteId());
        requestMember.setTeamId(this.getMarketTeamId());
        requestMember.setMarketerId(this.getMarketerId());
        requestMember.setAccount(this.getAccount());
        requestMember.setAccountType(1);
        requestMember.setAvatar(this.getAvatar());
        requestMember.setAgentId(this.getAgentId());
        requestMember.setVipId(this.getVipId());
        requestMember.setPhone(this.getPhone());
        requestMember.setPhoneStatus(this.getPhoneStatus());
        requestMember.setEmail(this.getEmail());
        requestMember.setEmailStatus(this.getEmailStatus());
        requestMember.setCashOutPwd(this.getCashOutPwd());
        requestMember.setCashOutStatus(this.getCashOutStatus());
        requestMember.setInviteRewardStatus(this.getInviteRewardStatus());
        requestMember.setUserType(this.getUserType().getType());

        return requestMember;
    }
}
