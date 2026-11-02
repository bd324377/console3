package com.console.product.dto;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.console.core.entity.AdminProc;
import com.console.core.model.req.BaseUpdateReq;
import com.console.framework.domain.IUpdateField;
import com.console.product.entity.ProductPlatform;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public class ProductPlatformReq {
    @Getter
    @Setter
    public static class UpdateProductPlatformReq extends BaseUpdateReq<ProductPlatform> implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String icon;                // 平台图标
        private Long offcialId;             // 官方id（原注释误写为"用户id"）
        private String platformName;        // 平台名称
        private Integer status;             // 状态
        private Integer sort;               // 排序
        private BigDecimal serviceRate;     // 服务费率

        @Getter
        public enum UpdateField implements IUpdateField<ProductPlatform> {
            ICON("icon", ProductPlatform::getIcon, "productPlatform.proc.update.icon"),
            OFFCIAL_ID("offcialId", ProductPlatform::getOfficialId, "productPlatform.proc.update.offcialId"),
            PLATFORM_NAME("platformName", ProductPlatform::getPlatformName, "productPlatform.proc.update.platformName"),
            STATUS("status", ProductPlatform::getStatus, "productPlatform.proc.update.status"),
            SORT("sort", ProductPlatform::getSort, "productPlatform.proc.update.sort"),
            SERVICE_RATE("serviceRate", ProductPlatform::getServiceRate, "productPlatform.proc.update.serviceRate");

            private final String fieldName;
            private final SFunction<ProductPlatform, Object> function;
            private final String descriptionKey;

            UpdateField(String fieldName, SFunction<ProductPlatform, Object> function, String descriptionKey) {
                this.fieldName = fieldName;
                this.function = function;
                this.descriptionKey = descriptionKey;
            }
        }

        @Override
        protected SFunction<ProductPlatform, ?> idFunction() {
            return ProductPlatform::getId;
        }

        @Override
        protected AdminProc.ProcBelong procBelong() {
            return AdminProc.ProcBelong.PRODUCT_PLATFORM;
        }

        @Override
        protected IUpdateField<ProductPlatform> parseField(String part) {
            return IUpdateField.resolve(UpdateField.class, part);
        }
    }
}
