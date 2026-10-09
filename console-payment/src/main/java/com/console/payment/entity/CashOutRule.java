package com.console.payment.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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

    @TableId(type=com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Integer id; // 主键ID
    private Integer marketTeamId; // 营销团队ID
    private Integer walletType; // 钱包类型；1、用户钱包；2、代理钱包
    private Integer ruleType; // 规则类型
    private String  ruleValue; // 规则阈值
    private Integer ruleStatus; // 规则开启状态；1、开启；0、关闭

    /**
     * 用户提现规则类型
     */
    @Getter
    public enum UserCashOutRuleType {
        UNKNOW_TYPE(0,"未知规则"),
        AUTOMATIC(1,"自动出款阈值"),
        RECHARGE_CASHOUT_RATE(2,"充提比率阈值"),
        ORDERED_RECHARGE_RATE(3,"下单和充值比率阈值");
        private final Integer key;
        private final String  value;

        UserCashOutRuleType(int key, String value) {
            this.key   = key;
            this.value = value;
        }

        public static String getRuleNameByKey(Integer ruleKey) {
            for (UserCashOutRuleType ruleType : UserCashOutRuleType.values()) {
                if (ruleType.key.equals(ruleKey)) {
                    return ruleType.getValue();
                }
            }
            return UNKNOW_TYPE.value;
        }
    }

    /**
     * 代理提现规则类型
     */
    @Getter
    public enum AgnetCashOutRuleType {
        UNKNOW_TYPE(0,"未知规则"),
        AUTOMATIC(1,"自动出款阈值"),
        RECHARGE_ORDERED_RATE(2,"直属下级下单和充值比率阈值");
        private final Integer key;
        private final String  value;

        AgnetCashOutRuleType(int key, String value) {
            this.key   = key;
            this.value = value;
        }

        public static String getRuleNameByKey(Integer ruleKey) {
            for (AgnetCashOutRuleType ruleType : AgnetCashOutRuleType.values()) {
                if (ruleType.key.equals(ruleKey)) {
                    return ruleType.getValue();
                }
            }
            return UNKNOW_TYPE.value;
        }
    }
}
