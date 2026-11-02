package com.console.product.dto;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.console.core.entity.AdminProc;
import com.console.core.model.req.BaseUpdateReq;
import com.console.framework.domain.IUpdateField;
import com.console.product.entity.Product;
import lombok.Getter;
import lombok.Setter;

public class ProductReq {
    @Getter
    @Setter
    public static class UpdateProductReq extends BaseUpdateReq<Product> {
        private Integer status; // 产品状态：1、正常；0、关闭
        private Integer sort;
        @Getter
        public enum UpdateField implements IUpdateField<Product> {
            STATUS("status", Product::getStatus, "product.proc.update.status"),
            SORT("sort", Product::getSort, "product.proc.update.sort");

            private final String fieldName;
            private final SFunction<Product, Object> function;
            private final String descriptionKey;

            UpdateField(String fieldName, SFunction<Product, Object> function, String descriptionKey) {
                this.fieldName = fieldName;
                this.function = function;
                this.descriptionKey = descriptionKey;
            }
        }
        @Override
        protected SFunction<Product, ?> idFunction() {
            return Product::getId;
        }

        @Override
        protected AdminProc.ProcBelong procBelong() {
            return AdminProc.ProcBelong.PRODUCT;
        }

        @Override
        protected IUpdateField<Product> parseField(String fieldName) {
            return IUpdateField.resolve(UpdateField.class,fieldName);
        }
    }
    @Getter
    @Setter
    public static class SearchProductReq {
        private Integer platformId; // 产商id
        private String productCode; // 产品代码
        private String productName; // 产品名称（json）
        private Integer status; // 产品状态：1、正常；0、关闭
    }
}
