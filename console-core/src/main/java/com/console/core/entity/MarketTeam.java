package com.console.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName(value = "s_market_team")
public class MarketTeam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer    id;                  //站点渠道ID
    private Integer    marketSiteId;        //站点ID
    private String     teamName;            //团队名称
    private Integer    idDefaultTeam;     //是否为站点默认团队（官方团队）
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
    private BigDecimal manualDepositedAmt;  //已人工入款金额
    private BigDecimal manualWithdrawnAmt;  //人工出款金额
    private BigDecimal withdrawalLimit;     //出款额度，额度不足直接进入审核状态
    private BigDecimal rechargeRate;        //充值手续费百分比
    private BigDecimal rechargeAmt;         //充值金额
    private BigDecimal rechargeFee;         //充值手续费
    private BigDecimal withdrawAmt;         //出款金额
    private BigDecimal orderRate;           //订单手续费率
    private BigDecimal orderAmt;            //已下单金额
    private BigDecimal orderResultAmt;      //订单盈亏金额
    private BigDecimal orderFee;            //订单手续费金额
    private BigDecimal profitAmt;           //团队利润金额
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;       //创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;       //更新时间
    private LocalDateTime closeTime;        //最近关闭时间
}
