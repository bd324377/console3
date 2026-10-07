package com.console.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.console.framework.constants.LoginChannel;
import com.console.framework.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Slf4j
@Getter
@Setter
@TableName("p_user_proc")
public class UserProc implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Integer userId;
    private ProcType procType;
    private LoginChannel loginChannel;//登录渠道
    private String ip;
    private String os;
    private String browser;
    private BigDecimal procValue;
    private BigDecimal orderResultAmt;
    private String  description;
    private Integer procDate;
    private Integer procTime;
    @TableField(exist = false)
    private Integer retryTimes;
    @TableField(exist = false)
    private Boolean fromRecovery;
    @TableField(exist = false)
    private Integer tenantId;

    public UserProc() {}

    public UserProc(Integer userId,Integer tenantId,ProcType procType) {
        this.userId      = userId;
        this.tenantId    = tenantId;
        this.procType    = procType;
        this.procTime    = DateUtils.getCurrentTimestamp();
        this.procDate    = DateUtils.getTodayTimestamp();
        this.description = procType.getValue();
    }

    @Getter
    public enum ProcType {//过程类型
        //0、登录；1、注册；2、充值；3、首充；4、下单；5、激活;6、用户钱包提现;7、代理钱包提现;8、补单充值;9、绑定手机号；10、绑定银行卡；11、绑定电子邮箱
        LONGIN(0,"登录", "userProc.login"),
        REGISTER(1,"注册", "userProc.register"),
        RECHARGE(2, "充值成功","userProc.recharge"),
        FIRST_RECHARGE(3,"首充成功", "userProc.firstRecharge"),
        PLACE_ORDER(4,"下单", "userProc.placeOrder"),
        ACTIVATE(5,"激活", "userProc.activate"),
        USER_CASH_OUT(6,"用户钱包提现","userProc.userCashOut"),
        AGENT_CASH_OUT(7,"代理钱包提现","userProc.agentCashOut"),
        RESUPPLY_RECHARGE(8,"补单充值","userProc.resupplyRecharge"),
        BIND_PHONE(9,"绑定手机","userProc.bindPhone"),
        BIND_BANK_CARD(10,"绑定银行卡","userProc.bindBankCard"),
        BIND_EMAIL(11,"绑定电子邮箱","userProc.bindEmail"),
        INITIATE_RECHARGE(12,"发起充值","userProc.initiateRecharge"),
        INITIATE_CASH_OUT(13,"发起提现","userProc.initiateCashOut"),
        CASH_OUT(14,"提现成功","userProc.cashOut");

        @EnumValue
        private final Integer key;
        private final String  value;
        private final String  langKey;//语言包键值

        ProcType(int key, String value, String langKey) {
            this.key     = key;
            this.value   = value;
            this.langKey = langKey;
        }
    }
}
