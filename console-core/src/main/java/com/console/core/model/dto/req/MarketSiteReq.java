package com.console.core.model.dto.req;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.console.core.entity.MarketSite;
import com.console.core.entity.AdminProc;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.FieldUtils;
import com.console.framework.utils.WrapperUtils;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

public class MarketSiteReq {
    @Getter
    @Setter
    public static class CreateMarketSiteReq {
        private Integer    tenantId;            //租户Id
        private String     siteName;            //站点名称
        private Integer    layoutId;            //站点布局类型ID
        private Integer    orderMode;           //下单模型
        private BigDecimal manualDepositLimit;  //人工入款额度
        private BigDecimal manualDepositedAmt;  //已人工入款金额
        private BigDecimal manualWithdrawnAmt;  //人工出款金额
        private BigDecimal withdrawalLimit;     //出款额度，额度不足直接进入审核状态
        private BigDecimal rechargeRate;        //充值手续费百分比
        private BigDecimal rechargeAmt;         //充值金额
        private BigDecimal rechargeFee;         //充值手续费
        private BigDecimal withdrawAmt;         //出款金额
        private BigDecimal orderRate;           //订单手续费百分比
        private BigDecimal orderAmt;            //已下单金额
        private BigDecimal orderResultAmt;      //订单盈亏金额
        private BigDecimal orderFee;            //订单手续费金额
        private BigDecimal profitAmt;           //站点利润金额
    }

    @Getter
    @Setter
    public static class UpdateMarketSiteReq {
        @NotNull
        private Integer siteId;//站点ID
        private String     siteName;            //站点名称
        private Integer    layoutId;            //站点布局类型ID
        private Integer    orderMode;           //下单模型
        private BigDecimal manualDepositLimit;  //人工入款额度
        private BigDecimal withdrawalLimit;     //出款额度，额度不足直接进入审核状态
        private BigDecimal rechargeRate;        //充值手续费百分比
        private BigDecimal orderRate;           //订单手续费百分比
        private MarketSite.SiteStatus status;   //站点状态：0-关闭，1-开启，3-维护中

        private UpdateWrapper<MarketSite> updateWrapper;
        private AdminProc adminProc;
        private String errorDesc;

        /**
         * 创建更新租户信息的参数和修改过程
         */
        public void buildUpdateParam(String part, MarketSite marketSite, RequestBackendUser backendUser) {
            if (marketSite == null) {
                errorDesc = "待修改站点不存在，请确认后重试";
                return;
            }
            UpdateField updateField = UpdateField.getUpdateFieldByField(part);
            updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(MarketSite::getId, marketSite.getId());
            // 统一设置字段
            Object newValue = FieldUtils.getFieldValue(this, updateField.getFieldName());
            SFunction<MarketSite, Object> function = updateField.getFunction();
            Object oldValue = function.apply(marketSite);
            if (Objects.equals(newValue, oldValue)) {
                errorDesc = "修改值和原值一样，无需修改，请确认后重试";
            }
            WrapperUtils.setPropertyValue(updateWrapper, function, newValue);
            adminProc = new AdminProc(backendUser, AdminProc.ProcBelong.MARKET_SITE,
                    AdminProc.ProcType.UPDATE, Long.valueOf(marketSite.getId()), oldValue,newValue);
        }

        @Getter
        public enum UpdateField {
            SITE_NAME("siteName",MarketSite::getSiteName,"site.proc.update.siteName"),
            LAYOUT_ID("layoutId",MarketSite::getLayoutId,"site.proc.update.layoutId"),
            ORDER_MODE("orderMode",MarketSite::getOrderMode,"site.proc.update.orderMode"),
            MANUAL_DEPOSIT_LIMIT("manualDepositLimit",MarketSite::getManualDepositLimit,"site.proc.update.manualDepositLimit"),
            WITHDRAWAL_LIMIT("withdrawalLimit",MarketSite::getWithdrawalLimit,"site.proc.update.withdrawalLimit"),
            RECHARGE_RATE("rechargeRate",MarketSite::getRechargeRate,"site.proc.update.rechargeRate"),
            ORDER_RATE("orderRate",MarketSite::getOrderRate,"site.proc.update.orderRate"),
            STATUS("status",MarketSite::getStatus,"site.proc.update.status");

            private final String fieldName;
            private final SFunction<MarketSite, Object> function;
            private final String descriptionKey;

            UpdateField(String fieldName,SFunction<MarketSite, Object> function,String descriptionKey) {
                this.fieldName = fieldName;
                this.function = function;
                this.descriptionKey = descriptionKey;
            }

            public static UpdateField getUpdateFieldByField(String fieldName) {
                for (UpdateField updateField : UpdateField.values()) {
                    if (updateField.fieldName.equals(fieldName)) {
                        return updateField;
                    }
                }
                throw new IllegalArgumentException("不支持的更新字段: " + fieldName);
            }
        }
    }
}
