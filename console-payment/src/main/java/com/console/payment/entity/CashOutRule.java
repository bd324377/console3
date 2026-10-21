package com.console.payment.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@TableName("s_cash_out_rule")
public class CashOutRule implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Integer id; // 主键ID
    private Integer marketTeamId; // 营销团队ID
    private Integer walletType; // 钱包类型；1、用户钱包；2、代理钱包
    private RuleType ruleType; // 规则类型
    private String ruleValue; // 规则阈值
    private Integer ruleStatus; // 规则开启状态；1、开启；0、关闭

    /**
     * 用户提现规则类型
     */
    @Getter
    public enum RuleType {
        UNKNOW_TYPE(0, "未知规则"),
        USER_AMT_AUTOMATIC(1, "自动出款阈值"),
        USER_RECHARGE_CASHOUT_RATE(2, "充提比率阈值"),
        USER_ORDERED_RECHARGE_RATE(3, "下单和充值比率阈值"),
        AGENT_AMT_AUTOMATIC(4, "直属下级充提比率阈值"),
        SUB_USER_USER_ORDERED_RECHARGE_RATE(5, "直属下级下单和充值比率阈值");

        @JsonValue
        @EnumValue
        private final Integer key;
        private final String value;

        RuleType(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
