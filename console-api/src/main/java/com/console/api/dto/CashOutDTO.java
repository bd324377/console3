package com.console.api.dto;

import com.console.core.entity.Wallet;
import com.console.payment.entity.CashOutOrder;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CashOutDTO {
    @Getter
    @Setter
    public static class CreateCashOutOrderReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @NotNull
        private Integer    bankCardId;      //银行卡ID
        @NotNull
        private Integer    channelId;       //渠道id、
        @NotNull
        private Wallet.WalletType walletType;      //出款钱包类型，1、玩家钱包；2、代理钱包
        @NotNull
        private BigDecimal amt;             //出款金额
        @NotNull
        private String     cashOutPwd;      //提现密码
        private String     phone;           //手机号
        private String     captcha;//验证码

        private Integer userId;
        private Integer riskValue = 0;//是否存在审核风险 数值越大，风险越高
    }

    /**
     * 提现订单查询参数
     */
    @Getter
    @Setter
    public static class SearchCashOutOrderReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private long    size = 20;          //每页条数
        private long    current = 1;        //当前分页
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startSearchTime;    //开始查询时间
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endSearchTime;      //结束查询时间
        private Integer status;              //订单状态，1、订单创建成功等待处理；2、审核通过；3、审核拒绝；4、出款中；5、提现成功；6、提现失败；7、订单取消；8、已退款;9、订单超时
    }

    /**
     * 提现密码修改
     */
    @Getter
    @Setter
    public static class CashOutPwdReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        @NotBlank
        private String password;
    }
}
