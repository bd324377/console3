package com.console.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.console.framework.constants.ValidationGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * vip信息表
 * </p>
 *
 * @author aha
 * @since 2024-09-24
 */
@Getter
@Setter
@TableName(value = "s_vip",autoResultMap = true)
public class Vip implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * vip主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String vipName;//vip等级名称
    private String icon;//图标
    private Integer level;//vip等级（不可重复）
    private BigDecimal upgradeAmt;//升级所需充值金额
    private BigDecimal upgradeOrderAmt;//升级所需下单金额
    private Integer cashOutStatus;//是否能提现：0、禁止提现1、自动提现；2、审核提现
    private BigDecimal cashOutRate;//提现手续费率
    private BigDecimal dayCashOutLimit;//日提现限额
    private BigDecimal rechargeRate;//充值手续费
    private String customerCares;//专属客服地址
    @TableField(exist = false)
    private Long nextVipId;//下一级VIP主键ID
}
