package com.console.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 产品类型表
 * </p>
 *
 * @author aha
 * @since 2024-09-24
 */
@Getter
@Setter
@TableName("s_product_type")
public class ProductType implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;//产品类型id
    private Integer marketSiteId;
    private Integer typeBasisId;//分类依据ID（产商分类和归属分类的时候填对应的ID）
    private String  typeCode;//分类标签
    private String  typeName;//类型名称
    private String  introduce;
    private String  topIcon;//分类顶部展示图标
    private String  subIcon;//分类副图标
    private String  bgImg;//分类背景图标
    private Integer sort;
    private Type    type;
    private Integer showType;
    private Integer isShowInTop;//是否在顶部展示 1、是 0、否
    private Integer showStatus;//展示状态 1、是 0、否

    @TableField(exist = false)
    private List<Integer> typeProductIdList;

    @Getter
    public enum Type {//类型 1、热门；2、最近 （1/2系统分类不可删除，只可修改排序）；3、自定义产品分类；4、自定义产商分类；5、产品产商列表
        UNKNOWN_TYPE(0,"未知类型"),
        HOT(1,"热门"),
        HISTORY(2,"历史"),
        PRODUCT(3,"自定义产品分类"),
        PLATFORM(4,"自定义产商分类"),
        PLATFORM_LIST(5,"自定义产商列表分类");

        @JsonValue
        @EnumValue
        private final Integer key;
        private final String  value;

        Type(int key, String value) {
            this.key   = key;
            this.value = value;
        }
    }
}
