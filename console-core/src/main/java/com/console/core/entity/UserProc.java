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
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private String ip;
    private String os;
    private String browser;
    private BigDecimal procValue;
    private BigDecimal orderResultAmt;
    private Integer orderTimes;
    private String  description;
    private LocalDate procDate;
    private LocalDateTime procTime;
    @TableField(exist = false)
    private Integer procCount;
    @TableField(exist = false)
    private Integer activateCount;

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
        this.procTime    = LocalDateTime.now();
        this.procDate    = LocalDate.now();
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
        RECEIVE_USER_REWARD(8,"领取用户活动奖励",""),
        RECEIVE_AGENT_REWARD(9,"领取用户活动奖励",""),
        VIP_UPDRADE(10,"VIP升级",""),
        AGENT_UPDRADE(11,"代理升级",""),
        BIND_PHONE(12,"绑定手机","userProc.bindPhone"),
        BIND_BANK_CARD(13,"绑定银行卡","userProc.bindBankCard"),
        BIND_EMAIL(14,"绑定电子邮箱","userProc.bindEmail"),
        UPDATE_LOGIN_PWD(15,"更新登录密码","userProc.updateLoginPwd");

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
