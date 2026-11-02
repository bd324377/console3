package com.console.product.dto;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.console.core.entity.AdminProc;
import com.console.core.model.req.BaseUpdateReq;
import com.console.framework.domain.IUpdateField;
import com.console.product.entity.ProductType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

public class ProductTypeReq {
    @Getter
    @Setter
    public static class AddProductTypeReq {
        @NotNull
        private Integer marketSiteId;
        private Integer typeBasisId;//分类依据ID（产商分类和归属分类的时候填对应的ID）
        @NotNull
        private String  typeCode;//分类标签
        private String  typeName;//类型名称
        private String  introduce;
        private String  topIcon;//分类顶部展示图标
        private String  subIcon;//分类副图标
        private String  bgImg;//分类背景图标
        private Integer sort;
        @NotNull
        private ProductType.Type type;
        private Integer showType;
        private Integer isShowInTop;//是否在顶部展示 1、是 0、否
        private Integer showStatus;//展示状态 1、是 0、否

        public ProductType buildProductType() {
            ProductType productType = new ProductType();
            BeanUtils.copyProperties(this,productType);
            return productType;
        }
    }

    @Getter
    @Setter
    public static class DelProductTypeReq {
        @NotNull
        private Integer productTypeId;
        @NotNull
        private Integer marketSiteId;
    }

    @Getter
    @Setter
    public static class UpdateProductTypeReq extends BaseUpdateReq<ProductType> {
        @NotNull
        private Integer marketSiteId;
        private Integer typeBasisId;//分类依据ID（产商分类和归属分类的时候填对应的ID）
        private String  typeCode;//分类标签
        private String  typeName;//类型名称
        private String  introduce;
        private String  topIcon;//分类顶部展示图标
        private String  subIcon;//分类副图标
        private String  bgImg;//分类背景图标
        private Integer sort;
        private Integer showType;
        private Integer isShowInTop;//是否在顶部展示 1、是 0、否
        private Integer showStatus;//展示状态 1、是 0、否

        @Getter
        public enum UpdateField implements IUpdateField<ProductType> {
            TYPE_BASIS_ID("typeBasisId",ProductType::getTypeBasisId,"productType.proc.update.picPath"),
            TYPE_CODE("typeCode",ProductType::getTypeCode,"productType.proc.update.title"),
            TYPE_NAME("typeName",ProductType::getTypeName,"productType.proc.update.content"),
            INTRODUCE("introduce",ProductType::getIntroduce,"productType.proc.update.routePath"),
            TOP_ICON("topIcon",ProductType::getTopIcon,"productType.proc.update.sort"),
            SUB_ICON("subIcon",ProductType::getSubIcon,"productType.proc.update.popUpRule"),
            BG_IMG("bgImg",ProductType::getBgImg,"productType.proc.update.title"),
            SORT("sort",ProductType::getSort,"productType.proc.update.content"),
            SHOW_TYPE("showType",ProductType::getShowType,"productType.proc.update.routePath"),
            IS_SHOW_IN_TOP("isShowInTop",ProductType::getIsShowInTop,"productType.proc.update.sort"),
            SHOW_STATUS("showStatus",ProductType::getShowStatus,"productType.proc.update.popUpRule");
            private final String fieldName;
            private final SFunction<ProductType, Object> function;
            private final String descriptionKey;

            UpdateField(String fieldName, SFunction<ProductType, Object> function, String descriptionKey) {
                this.fieldName = fieldName;
                this.function = function;
                this.descriptionKey = descriptionKey;
            }
        }

        @Override
        protected SFunction<ProductType, ?> idFunction() {
            return ProductType::getId;
        }

        @Override
        protected AdminProc.ProcBelong procBelong() {
            return AdminProc.ProcBelong.PRODUCT_TYPE;
        }

        @Override
        protected IUpdateField<ProductType> parseField(String fieldName) {
            return IUpdateField.resolve(UpdateField.class,fieldName);
        }
    }

    @Getter
    @Setter
    public static class SearchProductTypeReq {
        @NotNull
        private Integer marketSiteId;
        private String  typeCode;//分类标签
        private String  typeName;//类型名称
        private ProductType.Type type;
        private Integer showType;
        private Integer isShowInTop;//是否在顶部展示 1、是 0、否
        private Integer showStatus;//展示状态 1、是 0、否
    }
}
