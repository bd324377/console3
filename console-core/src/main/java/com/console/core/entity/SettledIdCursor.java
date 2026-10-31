package com.console.core.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@TableName("b_settled_id_cursor")
public class SettledIdCursor implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;
    private SettlementType settlementType;//结算类型ID
    private Long settledId;//已结算ID

    @Getter
    public enum SettlementType {
        PRODUCT_ORDER("product_order","产品订单结算汇总"),
        USER_PROC("user_proc","用户过程结算汇总"),
        WALLET_PROC("wallet_proc","钱包过程结算汇总"),
        ACTIVITY_PROC("activity_proc","活动过程结算汇总"),
        PAYMENT_PROC("payment_proc","支付过程结算汇总"),
        CASHOUT_PROC("cash_out_proc","提现过程结算汇总"),
        AGENT_LAYER_SYNC("agent_layer_sync","代理关系同步"),
        USER_PROC_SYNC("user_proc_sync","用户行为同步到总租户");

        @JsonValue
        @EnumValue
        private final String key;
        private final String desc;

        SettlementType(String key,String desc) {
            this.key = key;
            this.desc = desc;
        }
    }
}
