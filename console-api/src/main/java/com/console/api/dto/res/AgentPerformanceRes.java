package com.console.api.dto.res;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AgentPerformanceRes {
    private Integer userId;
    private Integer parentId;
    private String  shareCode;
    private Integer inviteeCount = 0;               //总邀新人数
    private Integer validInviteeCount = 0;          //
    private Integer oneLayerValidInviteeCount = 0;       //直属下级邀新人数
    private Integer otherLayerValidInviteeCount = 0;     //其他层级邀新人数

    private BigDecimal orderAmt = BigDecimal.ZERO;//总业绩
    private BigDecimal oneLayerOrderAmt   = BigDecimal.ZERO; //直属下级业绩
    private BigDecimal otherLayerOrderAmt = BigDecimal.ZERO; //其他成绩业绩

    private BigDecimal agentRewardAmt = BigDecimal.ZERO;          //总佣金
    private BigDecimal inviteNewRewardAmt = BigDecimal.ZERO;      //邀新宝箱奖励
    private BigDecimal orderRakeBackAmt   = BigDecimal.ZERO;        //订单返佣奖励
    private BigDecimal unReceiveRewardAmt = BigDecimal.ZERO;  //未领取的活动奖励金额

    private BigDecimal rechargeAmt = BigDecimal.ZERO;             // 存款金额
    private BigDecimal oneLayerRechargeAmt = BigDecimal.ZERO;     // 直属充值金额
    private BigDecimal otherLayerRechargeAmt = BigDecimal.ZERO;   // 其他层级充值金额
}
