package com.console.product.model.dto;

import com.console.product.entity.ProductType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ProductTypeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer typeBasisId;//分类依据ID（产商分类和归属分类的时候填对应的ID）
    private String  typeCode;//分类标签
    private String  topIcon;//分类顶部展示图标
    private String  subIcon;//分类副图标
    private String  bgImg;//分类背景图标
    private ProductType.Type type;
    private Integer showType;
    private Integer isShowInTop;//是否在顶部展示 1、是 0、否
    private Integer showStatus;//展示状态 1、是 0、否
    private List<Integer> typeProductIdList;
}
