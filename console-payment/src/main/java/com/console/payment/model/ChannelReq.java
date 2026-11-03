package com.console.payment.model;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.console.core.entity.AdminProc;
import com.console.core.model.req.BaseUpdateReq;
import com.console.framework.domain.IUpdateField;
import com.console.payment.entity.Channel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

public class ChannelReq {

    @Getter
    @Setter
    public static class DelChannelReq {
        @NotNull
        private Integer channelId;
        @NotNull
        private String merchantId;
    }

    @Getter
    @Setter
    public static class UpdateChannelReq extends BaseUpdateReq<Channel> {
        @NotNull
        private Integer id;
        @NotBlank
        private String merchantId;//支付商户key
        private String channelName;     //渠道名称
        private String icon;            //渠道图标
        private String showName;        //前端展示渠道名称
        private Integer sort;           //渠道排序
        private String curType;         //币种
        private Channel.AmtUnit amtUnit;        //金额单位
        private Integer payStatus;      //支付渠道状态：1、开启；0、关闭
        private Integer realNamePayStatus;   //是否实名支付状态：1、实名支付；0、无需实名支付
        private Integer cashOutStatus;  //提现渠道状态：1、开启；0、关闭；
        private Channel.ChannelBelong belongClass;//支付/提现渠道归属类
        private String payType;//渠道支付方式类型
        private String payUrl;//支付url
        private String payCallBackUrl;//支付回调URL
        private String paySearchUrl;//支付订单查询url
        private String cashOutUrl;//提现url
        private String cashOutCallBackUrl;//代付回调URL
        private String cashOutSearchUrl;//代付订单查询url
        private String balanceQueryUrl;//渠道余额查询url
        private String apiSecret;//支付商户secret
        private BigDecimal minPayAmt;//单笔最小充值金额
        private BigDecimal maxPayAmt;//单笔最大充值金额
        private BigDecimal minCashOutAmt;//单笔最小提现金额
        private BigDecimal maxCashOutAmt;//单笔最大提现金额
        private BigDecimal payFeeRate;//支付手续费率
        private BigDecimal cashOutFeeRate;//提现手续费率
        private Integer isFixedAmt;//是否只支持固定充值金额 1、是；0、否
        private List<String> payAmtArr;//展示的固定金额数组，多个以分号隔开
        private List<String> cashOutAmtArr;//展示的固定金额数组，多个以分号隔开
        private List<String> whiteIps;//回调白名单
        private BigDecimal toBeOrderRate;//充值下单倍率
        private String remark;//备注

        @Getter
        public enum UpdateField implements IUpdateField<Channel> {
            CHANNEL_NAME("channelName",Channel::getChannelName,"channle.proc.update.channelName"),
            ICON("icon", Channel::getIcon, "channle.proc.update.icon"),
            SHOW_NAME("showName", Channel::getShowName, "channle.proc.update.showName"),
            SORT("sort", Channel::getSort, "channle.proc.update.sort"),
            CUR_TYPE("curType", Channel::getCurType, "channle.proc.update.curType"),
            AMT_UNIT("amtUnit", Channel::getAmtUnit, "channle.proc.update.amtUnit"),
            PAY_STATUS("payStatus", Channel::getPayStatus, "channle.proc.update.payStatus"),
            REAL_NAME_PAY_STATUS("realNamePayStatus", Channel::getRealNamePayStatus, "channle.proc.update.realNamePayStatus"),
            CASH_OUT_STATUS("cashOutStatus", Channel::getCashOutStatus, "channle.proc.update.cashOutStatus"),
            BELONG_CLASS("belongClass", Channel::getBelongClass, "channle.proc.update.belongClass"),
            PAY_TYPE("payType", Channel::getPayType, "channle.proc.update.payType"),
            PAY_URL("payUrl", Channel::getPayUrl, "channle.proc.update.payUrl"),
            PAY_CALL_BACK_URL("payCallBackUrl", Channel::getPayCallBackUrl, "channle.proc.update.payCallBackUrl"),
            PAY_SEARCH_URL("paySearchUrl", Channel::getPaySearchUrl, "channle.proc.update.paySearchUrl"),
            CASH_OUT_URL("cashOutUrl", Channel::getCashOutUrl, "channle.proc.update.cashOutUrl"),
            CASH_OUT_CALL_BACK_URL("cashOutCallBackUrl", Channel::getCashOutCallBackUrl, "channle.proc.update.cashOutCallBackUrl"),
            CASH_OUT_SEARCH_URL("cashOutSearchUrl", Channel::getCashOutSearchUrl, "channle.proc.update.cashOutSearchUrl"),
            BALANCE_QUERY_URL("balanceQueryUrl", Channel::getBalanceQueryUrl, "channle.proc.update.balanceQueryUrl"),
//            MERCHANT_ID("merchantId", Channel::getMerchantId, "channle.proc.update.merchantId"),
            API_SECRET("apiSecret", Channel::getApiSecret, "channle.proc.update.apiSecret"),
            MIN_PAY_AMT("minPayAmt", Channel::getMinPayAmt, "channle.proc.update.minPayAmt"),
            MAX_PAY_AMT("maxPayAmt", Channel::getMaxPayAmt, "channle.proc.update.maxPayAmt"),
            MIN_CASH_OUT_AMT("minCashOutAmt", Channel::getMinCashOutAmt, "channle.proc.update.minCashOutAmt"),
            MAX_CASH_OUT_AMT("maxCashOutAmt", Channel::getMaxCashOutAmt, "channle.proc.update.maxCashOutAmt"),
            PAY_FEE_RATE("payFeeRate", Channel::getPayFeeRate, "channle.proc.update.payFeeRate"),
            CASH_OUT_FEE_RATE("cashOutFeeRate", Channel::getCashOutFeeRate, "channle.proc.update.cashOutFeeRate"),
            IS_FIXED_AMT("isFixedAmt", Channel::getIsFixedAmt, "channle.proc.update.isFixedAmt"),
            PAY_AMT_ARR("payAmtArr", Channel::getPayAmtArr, "channle.proc.update.payAmtArr"),
            CASH_OUT_AMT_ARR("cashOutAmtArr", Channel::getPayAmtArr, "channle.proc.update.cashOutAmtArr"),
            WHITE_IPS("whiteIps", Channel::getWhiteIps, "channle.proc.update.whiteIps"),
            TO_BE_ORDER_RATE("toBeOrderRate", Channel::getToBeOrderRate, "channle.proc.update.toBeOrderRate"),
            REMARK("remark", Channel::getRemark, "channle.proc.update.remark");

            private final String fieldName;
            private final SFunction<Channel, Object> function;
            private final String descriptionKey;

            UpdateField(String fieldName, SFunction<Channel, Object> function, String descriptionKey) {
                this.fieldName = fieldName;
                this.function = function;
                this.descriptionKey = descriptionKey;
            }
        }

        @Override
        protected SFunction<Channel, ?> idFunction() {
            return Channel::getId;
        }

        @Override
        protected AdminProc.ProcBelong procBelong() {
            return AdminProc.ProcBelong.CHANNEL;
        }

        @Override
        protected IUpdateField<Channel> parseField(String fieldName) {
            return IUpdateField.resolve(UpdateField.class,fieldName);
        }
    }

    @Getter
    @Setter
    public static class SearchChannelReq {
        private String channelName;
        private String showName;        //前端展示渠道名称
        private String merchantId;//支付商户key
    }
}
