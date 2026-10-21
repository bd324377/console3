package com.console.core.model.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class MarketTeamReq {

    @Getter
    @Setter
    public static class CreateMarketTeamReq {
        @NotNull
        private Integer    marketSiteId;        //站点ID
        @NotNull
        private String     teamName;            //团队名称
        @NotNull
        private Integer    leaderMarketerId;    //渠道组长ID（关联营销人员表）
        private Integer    status;              //站点状态：0-关闭，1-开启，3-维护中，4-冻结，5-审核中，6-已过期
        private Integer    layoutId;            //前端布局配置ID
        private Integer    orderMode;           //下单模型
        private BigDecimal activationRechargeAmt;//激活所需充值金额
        private BigDecimal activationOrderAmt;  //激活所需下单金额
        private Integer    shuntStatus;         //分流状态
        private BigDecimal shuntRate;           //分流率
        private Integer    shuntThreshold;      //分流阈值
        @NotNull
        private BigDecimal manualDepositLimit;  //人工入款额度
        @NotNull
        private BigDecimal withdrawalLimit;     //出款额度，额度不足直接进入审核状态
        @NotNull
        private BigDecimal rechargeRate;        //充值手续费百分比
        @NotNull
        private BigDecimal orderRate;           //订单手续费率
    }

    @Getter
    @Setter
    public static class UpdateMarketTeam {
        @NotNull
        private Integer    id;    //团队ID
        private String     teamName;            //团队名称
        private Integer    leaderMarketerId;    //渠道组长ID（关联营销人员表）
        private Integer    status;              //站点状态：0-关闭，1-开启，3-维护中，4-冻结，5-审核中，6-已过期
        private Integer    layoutId;            //前端布局配置ID
        private Integer    orderMode;           //下单模型
        private BigDecimal activationRechargeAmt;//激活所需充值金额
        private BigDecimal activationOrderAmt;  //激活所需下单金额
        private Integer    shuntStatus;         //分流状态
        private BigDecimal shuntRate;           //分流率
        private Integer    shuntThreshold;      //分流阈值
        private BigDecimal manualDepositLimit;  //人工入款额度
        private BigDecimal withdrawalLimit;     //出款额度，额度不足直接进入审核状态
        private BigDecimal rechargeRate;        //充值手续费百分比
        private BigDecimal orderRate;           //订单手续费率
    }
}
