package com.console.core.model.dto.req;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.console.core.entity.MarketSiteDomain;
import com.console.core.entity.AdminProc;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.FieldUtils;
import com.console.framework.utils.WrapperUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public class MarketSiteDomainReq {
    @Getter
    @Setter
    public static class CreateMarketSiteDomainReq {
        @NotNull
        private Integer siteId;//站点ID
        @NotBlank
        private String  siteDomain;//站点域名
    }

    @Getter
    @Setter
    public static class UpdateMarketSiteDomainReq {
        @NotNull
        private Integer id;//站点与域名关系ID
        @NotNull
        private Integer siteId;//站点ID
        private String  siteDomain;//站点域名
        private Integer status;//域名使用状态

        private UpdateWrapper<MarketSiteDomain> updateWrapper;
        private AdminProc adminProc;
        private String    errorDesc;

        public void buildUpdateParam(String part, MarketSiteDomain siteDomain, RequestBackendUser backendUser) {
            if (siteDomain == null) {
                errorDesc = "待修改站点域名不存在，请确认后重试";
                return;
            }
            UpdateField updateField = UpdateField.getUpdateFieldByField(part);
            updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(MarketSiteDomain::getId, siteDomain.getId());
            // 统一设置字段
            Object newValue = FieldUtils.getFieldValue(this, updateField.getFieldName());
            SFunction<MarketSiteDomain,Object> function = updateField.getFunction();
            Object oldValue = function.apply(siteDomain);
            if (Objects.equals(newValue, oldValue)) {
                errorDesc = "修改值和原值一样，无需修改，请确认后充值";
            }
            WrapperUtils.setPropertyValue(updateWrapper, function, newValue);
            adminProc = new AdminProc(backendUser, AdminProc.ProcBelong.MARKET_SITE_DOMAIN, AdminProc.ProcType.UPDATE, Long.valueOf(siteDomain.getId()), oldValue,newValue);
        }

        @Getter
        public enum UpdateField {
            SITE_DOMAIN("siteDomain",MarketSiteDomain::getSiteDomain,"siteDomain.proc.update.siteDomain"),
            STATUS("status",MarketSiteDomain::getStatus,"siteDomain.proc.update.status");

            private final String fieldName;
            private final SFunction<MarketSiteDomain,Object> function;
            private final String descriptionKey;

            UpdateField(String fieldName, SFunction<MarketSiteDomain,Object> function, String descriptionKey) {
                this.fieldName = fieldName;
                this.function  = function;
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
