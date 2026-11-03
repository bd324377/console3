package com.console.admin.model.req;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.console.admin.entity.CustomerCare;
import com.console.core.entity.AdminProc;
import com.console.core.model.req.BaseUpdateReq;
import com.console.framework.domain.IUpdateField;
import com.console.product.entity.ProductType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

public class CustomerCareReq {
    @Getter
    @Setter
    public static class AddCustomerCareReq {
        private Integer marketSiteId;
        private Integer marketTeamId;
        private Integer marketerId;

        @NotBlank
        private String customerCareName;        // 客服名称
        @NotBlank
        private String icon;                    // 客服图标
        @NotBlank
        private String routePath;               // 客服路由
        @NotBlank
        private String desc;                    // 客服描述
        private Integer sort;                   //排序值
    }


    @Getter
    @Setter
    public static class UpdateCustomerCareReq extends BaseUpdateReq<CustomerCare> {
        @NotNull
        private Integer id;
        private String customerCareName;        // 客服名称
        private String icon;                    // 客服图标
        private String routePath;               // 客服路由
        private String desc;                    // 客服描述
        private Integer sort;                   //排序值

        @Getter
        public enum UpdateField implements IUpdateField<CustomerCare> {
            CUSTOMER_CARE_NAME("customerCareName",CustomerCare::getCustomerCareName,"customerCare.proc.update.customerCareName"),
            ICON("icon",CustomerCare::getIcon,"customerCare.proc.update.icon"),
            ROUTE_PATH("routePath",CustomerCare::getRoutePath,"customerCare.proc.update.routePath"),
            DESC("desc",CustomerCare::getDesc,"customerCare.proc.update.desc"),
            SOET("sort",CustomerCare::getSort,"customerCare.proc.update.sort");

            private final String fieldName;
            private final SFunction<CustomerCare, Object> function;
            private final String descriptionKey;

            UpdateField(String fieldName, SFunction<CustomerCare, Object> function, String descriptionKey) {
                this.fieldName = fieldName;
                this.function = function;
                this.descriptionKey = descriptionKey;
            }
        }

        @Override
        protected SFunction<CustomerCare, ?> idFunction() {
            return CustomerCare::getId;
        }

        @Override
        protected AdminProc.ProcBelong procBelong() {
            return AdminProc.ProcBelong.CUSTOMER_CARE;
        }

        @Override
        protected IUpdateField<CustomerCare> parseField(String fieldName) {
            return IUpdateField.resolve(UpdateField.class,fieldName);
        }
    }
}
