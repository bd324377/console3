package com.console.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName(value = "s_marketer")
public class Marketer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;//管理员与站点关系ID
    private Integer marketSiteId;//站点ID
    private Integer marketTeamId;//营销团队ID
    private Integer marketerId;//业务员（管理员）ID
    private Integer isLeader;//是否为渠道组长：0-否，1-是
    private Integer orderMode;//下单模型
    private BigDecimal rechargeRate;//充值手续费百分比
    private BigDecimal manualDepositLimit;//人工入款额度
    private BigDecimal manualDepositedAmt;//已人工入款金额
    private BigDecimal manualWithdrawnAmt;//人工出款金额
    private BigDecimal withdrawalLimit;//出款额度，额度不足直接进入审核状态
    private BigDecimal rechargeAmt;//充值金额
    private BigDecimal rechargeFee;//充值手续费
    private BigDecimal withdrawAmt;//出款金额
    private BigDecimal orderAmt;//已下单金额
    private BigDecimal orderResultAmt;//订单盈亏金额
    private BigDecimal orderFee;//订单手续费金额
    private BigDecimal profitAmt;//站点利润金额
    private MarketerStatus status;//业务员状态 停用后使用其分享链接注册自动归属到其所在渠道
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//更新时间

    @TableField(exist = false)
    private Integer tenantId;//租户ID

    @Getter
    public enum MarketerStatus {
        UNENABLE(0, "停用"),
        ENABLE(1, "启用");

        private final Integer value;
        private final String desc;

        MarketerStatus(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }
    }
}
