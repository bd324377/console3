package com.console.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName(value = "s_market_site")
public class MarketSite implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Integer    id;                  //站点ID
    private Integer    tenantId;            //租户编码
    private String     siteName;            //站点名称
    private Integer    layoutId;            //站点布局类型ID
    private Integer    orderMode;           //下单模型
    private BigDecimal manualDepositLimit;  //人工入款额度
    private BigDecimal manualDepositedAmt;  //已人工入款金额
    private BigDecimal manualWithdrawnAmt;  //人工出款金额
    private BigDecimal withdrawalLimit;     //出款额度，额度不足直接进入审核状态
    private BigDecimal rechargeRate;        //充值手续费百分比
    private BigDecimal rechargeAmt;         //充值金额
    private BigDecimal rechargeFee;         //充值手续费
    private BigDecimal withdrawAmt;         //出款金额
    private BigDecimal orderRate;           //订单手续费百分比
    private BigDecimal orderAmt;            //已下单金额
    private BigDecimal orderResultAmt;      //订单盈亏金额
    private BigDecimal orderFee;            //订单手续费金额
    private BigDecimal profitAmt;           //站点利润金额
    private SiteStatus status;              //站点状态：0-关闭，1-开启，3-维护中
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;       //创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;       //更新时间
    private LocalDateTime lastOpenTime;     //最近开放时间
    private LocalDateTime lastCloseTime;    //最近关闭时间

    @Getter
    public enum SiteStatus {
        CLOSED(0, "关闭"),
        OPEN(1, "开启"),
        MAINTENANCE(3, "维护中");

        @JsonValue
        @EnumValue
        private final Integer value;
        private final String desc;

        SiteStatus(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }
    }
}
