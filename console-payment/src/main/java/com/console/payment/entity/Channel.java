package com.console.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.console.framework.constants.ValidationGroup;
import com.console.payment.channel.strategy.fourz.res.OrderStatus;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@TableName(value = "s_channel", autoResultMap = true)
public class Channel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;                //渠道id
    private Integer marketSiteId;   //站点ID
    @NotBlank(message = "",groups = ValidationGroup.insert.class)
    private String channelName;     //渠道名称
    private String icon;            //渠道图标
    @NotBlank(message = "",groups = ValidationGroup.insert.class)
    private String showName;        //前端展示渠道名称
    private Integer sort;           //渠道排序
    @NotBlank(message = "",groups = ValidationGroup.insert.class)
    private String curType;         //币种
    private AmtUnit amtUnit;        //金额单位
    private Integer payStatus;      //支付渠道状态：1、开启；0、关闭
    private Integer realNamePayStatus;   //是否实名支付状态：1、实名支付；0、无需实名支付
    private Integer cashOutStatus;  //提现渠道状态：1、开启；0、关闭；
    private String belongClass;//支付/提现渠道归属类
    @NotBlank(message = "",groups = ValidationGroup.insert.class)
    private String payType;//渠道支付方式类型
    @NotBlank(message = "",groups = ValidationGroup.insert.class)
    private String payUrl;//支付url
    @NotBlank(message = "",groups = ValidationGroup.insert.class)
    private String payCallBackUrl;//支付回调URL
    @NotBlank(message = "",groups = ValidationGroup.insert.class)
    private String paySearchUrl;//支付订单查询url
    @NotBlank(message = "",groups = ValidationGroup.insert.class)
    private String cashOutUrl;//提现url
    @NotBlank(message = "",groups = ValidationGroup.insert.class)
    private String cashOutCallBackUrl;//代付回调URL
    @NotBlank(message = "",groups = ValidationGroup.insert.class)
    private String cashOutSearchUrl;//代付订单查询url
    private String balanceQueryUrl;//渠道余额查询url
    @NotBlank(message = "",groups = ValidationGroup.insert.class)
    private String appKey;//支付商户key
    @NotBlank(message = "",groups = ValidationGroup.insert.class)
    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY)
    private String apiSecret;//支付商户secret
    private BigDecimal minPayAmt;//单笔最小充值金额
    private BigDecimal maxPayAmt;//单笔最大充值金额
    private BigDecimal minCashOutAmt;//单笔最小提现金额
    private BigDecimal maxCashOutAmt;//单笔最大提现金额
    private BigDecimal payFeeRate;//支付手续费率
    private BigDecimal cashOutFeeRate;//提现手续费率
    private Integer isFixedAmt;//是否只支持固定充值金额 1、是；0、否
    private String payAmtArr;//展示的固定金额数组，多个以分号隔开
    @TableField(value = "white_ips",typeHandler = JacksonTypeHandler.class)
    private List<String> whiteIps;//回调白名单
    private BigDecimal toBeOrderRate;//充值下单倍率
    private String remark;//备注

    @Getter
    public enum AmtUnit {
        YUAN(0,"元"),
        JIAO(1,"角"),
        FEN(2,"分");

        @JsonValue
        @EnumValue
        private final Integer key;
        private final String desc;

        AmtUnit(int key,String desc) {
            this.key = key;
            this.desc = desc;
        }
    }
}
