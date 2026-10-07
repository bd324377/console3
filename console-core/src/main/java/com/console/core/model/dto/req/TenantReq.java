package com.console.core.model.dto.req;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.console.core.entity.AdminProc;
import com.console.core.entity.Tenant;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.FieldUtils;
import com.console.framework.utils.WrapperUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class TenantReq {
    @Getter
    @Setter
    public static class CreateTenantReq {
        @NotBlank
        private String tenantName;              //租户名称
        private Integer layoutId;                //默认布局配置ID
        private String domains;                 //前端绑定域名（JSON数组格式）
        private String backendDomains;          //后端绑定域名（JSON数组格式）
        private String timeZone;                //时区
        private String whiteIps;                //IP白名单列表（JSON数组格式）
        private String backendLimitIps;         //后端限制登录IP（JSON数组格式，空则不限制，有值则表示只有设定值能登录后台）
        private String pwaDownLoadDomain;       //PWA下载域名（JSON数组格式，随机跳转到对应域名）
        private String webPushConfig;           //推送配置（JSON格式）
        private String webVersion;              //前端版本号
        private String backendWebVersion;       //管理后台前端版本号
        private Integer orderMode;               //下单模型
        private BigDecimal manualDepositLimit;      //人工入款额度
        private BigDecimal manualDepositedAmt;      //已人工入款金额
        private BigDecimal manualWithdrawnAmt;      //人工出款金额
        private BigDecimal withdrawalLimit;         //出款额度，额度不足直接进入审核状态
        private BigDecimal rechargeRate;            //充值手续费百分比
        private BigDecimal rechargeAmt;             //充值金额
        private BigDecimal rechargeFee;             //充值手续费
        private BigDecimal withdrawAmt;             //出款金额
        private BigDecimal orderRate;               //订单手续费百分比
        private BigDecimal orderAmt;                //已下单金额
        private BigDecimal orderResultAmt;          //订单盈亏金额
        private BigDecimal orderFee;                //订单手续费金额
        private BigDecimal profitAmt;               //租户利润金额
        private Tenant.TenantStatus status;         //租户状态：0-不对外开放（白名单人员才能进入），1-对外开放，2-暂停开放（可正常登录，但前端用户无法进入产品页面）
        private String     remark;                  //备注
    }
    @Getter
    @Setter
    public static class UpdateTenantReq {
        @NotNull
        private Integer tenantId;//租户ID
        private String tenantName;
        private Integer layoutId;                //默认布局配置ID
        private List<String> domains;                 //前端绑定域名（JSON数组格式）
        private String backendDomains;          //后端绑定域名（JSON数组格式）
        private String timeZone;                //时区
        private String whiteIps;                //IP白名单列表（JSON数组格式）
        private String backendLimitIps;         //后端限制登录IP（JSON数组格式，空则不限制，有值则表示只有设定值能登录后台）
        private String pwaDownLoadDomain;       //PWA下载域名（JSON数组格式，随机跳转到对应域名）
        private String webPushConfig;           //推送配置（JSON格式）
        private String webVersion;              //前端版本号
        private String backendWebVersion;       //管理后台前端版本号
        private Integer orderMode;               //下单模型
        private BigDecimal manualDepositLimit;      //人工入款额度
        private BigDecimal withdrawalLimit;         //出款额度，额度不足直接进入审核状态
        private BigDecimal rechargeRate;            //充值手续费百分比
        private BigDecimal orderRate;               //订单手续费百分比
        private String     remark;                  //备注

        private UpdateWrapper<Tenant> updateWrapper;
        private AdminProc adminProc;
        private String errorDesc;//错误描述

        /**
         * 创建更新租户信息的参数和修改过程
         */
        public void buildUpdateParam(String part, Tenant tenant, RequestBackendUser backendUser) {
            if (tenant == null) {
                errorDesc = "待修改租户不存在，请确认后重试";
                return;
            }
            UpdateField updateField = UpdateField.getUpdateFieldByField(part);
            updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(Tenant::getId, tenant.getId());
            // 统一设置字段
            Object newValue = FieldUtils.getFieldValue(this, updateField.getFieldName());
            SFunction<Tenant, Object> function = updateField.getFunction();
            Object oldValue = function.apply(tenant);
            if (Objects.equals(newValue, oldValue)) {
                errorDesc = "修改值和原值一样，无需修改，请确认后充值";
            }
            WrapperUtils.setPropertyValue(updateWrapper, function, newValue);
            adminProc = new AdminProc(backendUser, AdminProc.ProcBelong.TENANT, AdminProc.ProcType.UPDATE, Long.valueOf(tenant.getId()), oldValue,newValue);
        }

        @Getter
        public enum UpdateField {
            TENANT_NAME("tenantName",Tenant::getTenantName, "tenant.proc.update.tenantName"),
            LAYOUT_ID("layoutId",Tenant::getLayoutId,"tenant.proc.update.layoutId"),
            DOMAINS("domains", Tenant::getDomains, "tenant.proc.update.domains"),
            BACKEND_DOMAINS("backendDomains", Tenant::getBackendDomains, "tenant.proc.update.backendDomains"),
            PWA_DOWNLOAD_DOMAIN("pwaDownLoadDomain", Tenant::getPwaDownLoadDomain, "tenant.proc.update.pwaDownLoadDomain"),
            WEB_PUSH_CONFIG("webPushConfig", Tenant::getWebPushConfig, "tenant.proc.update.webPushConfig"),
            STATUS("status", Tenant::getStatus, "tenant.proc.update.status"),
            TIME_ZONE("timeZone", Tenant::getTimeZone, "tenant.proc.update.timeZone"),
            WEB_VERSION("webVersion", Tenant::getWebVersion, "tenant.proc.update.webVersion"),
            BACKEND_WEB_VERSION("backendWebVersion", Tenant::getBackendWebVersion, "tenant.proc.update.backendWebVersion"),
            WHITE_IPS("whiteIps", Tenant::getWhiteIps, "tenant.proc.update.whiteIps"),
            BACKEND_LIMIT_IPS("backendLimitIps", Tenant::getBackendLimitIps, "tenant.proc.update.backendLimitIps"),
            ORDER_MODE("orderMode", Tenant::getOrderMode, "tenant.proc.update.orderMode"),
            RECHARGE_RATE("rechargeRate", Tenant::getRechargeRate, "tenant.proc.update.rechargeRate"),
            MANUAL_DEPOSIT_LIMIT("manualDepositLimit", Tenant::getManualDepositLimit, "tenant.proc.update.manualDepositLimit"),
            WITHDRAWAL_LIMIT("withdrawalLimit", Tenant::getWithdrawalLimit, "tenant.proc.update.withdrawalLimit"),
            ORDER_RATE("orderRate", Tenant::getOrderRate, "tenant.proc.update.orderRate"),
            REMARK("remark", Tenant::getRemark, "tenant.proc.update.remark");

            private final String fieldName;
            private final SFunction<Tenant, Object> function;
            private final String descriptionKey;

            UpdateField(String fieldName,SFunction<Tenant, Object> function, String descriptionKey) {
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
