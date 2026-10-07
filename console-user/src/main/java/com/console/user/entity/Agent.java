package com.console.user.entity;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.console.framework.constants.ValidationGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 代理表
 * </p>
 *
 * @author aha
 * @since 2024-09-24
 */
@Getter
@Setter
@TableName("s_agent")
public class Agent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer id;
    @NotBlank(message = "", groups = ValidationGroup.insert.class)
    private String agentName;
    private String customerCares;//代理专属客服列表
    private String icon;
    private Integer level;
    private BigDecimal upgradeAmt;
    private BigDecimal upgradeOrderAmt;
    private Integer cashOutStatus;//是否能提现：0、禁止提现1、自动提现；2、审核提现
    private BigDecimal cashOutRate;//提现手续费率
    private BigDecimal dayCashOutLimit;//日提现限额
    private BigDecimal transferOrderRate;//转账到用户钱包需下单比例，负数则不允许转账
    private Integer rakeBackState;//返佣状态
    private Integer rakeBackRange;//返佣范围（正数为充值返佣，负数为订单返佣）  -1、订单返佣；0、每笔都有奖励；1、只有首充有奖励；2、只有前两笔充值有奖励；3、每日的前N比充值有奖励
    private Integer dailyRakeBackNum;//每日返佣笔数(返佣范围为3时启用该参数)
    private String  rakeBackRates;//返佣率

    @TableField(exist = false)
    private Long nextAgentId;

    public List<RakeBackRate> getRakeBackRates() {
        if (StringUtils.hasText(this.rakeBackRates)) {
            return JSON.parseArray(this.rakeBackRates,RakeBackRate.class);
        }
        return null;
    }

    @Getter
    @Setter
    public static class RakeBackRate implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private Integer layer;//层级
        private BigDecimal rakeBackRate;//返佣比率
    }
}
