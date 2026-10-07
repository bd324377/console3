package com.console.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@TableName(value = "r_channel_agent",autoResultMap = true)
public class ChannelAgent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Integer      id;
    private Integer      channelId;//渠道ID 0、官方渠道
    private Integer      agentId;//代理等级ID 0、通用
    private BigDecimal   validInviteeAmt;//有效受邀人充值判定金额
    private BigDecimal   validInviteeOrderAmt;//有效受邀人下单判定金额
    private List<String> shareDescs;//分享文案
    @TableField(value = "customer_cares",typeHandler = JacksonTypeHandler.class)
    private List<AgentCustomerCare> customerCares;//代理专属客服列表

    @Getter
    @Setter
    public static class AgentCustomerCare implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private String showImg;
        private String careName;
        private String routePath;
    }
}
