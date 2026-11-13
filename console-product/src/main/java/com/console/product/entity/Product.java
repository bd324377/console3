package com.console.product.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.console.framework.utils.TenantUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("s_product")
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id; // 产品id
    @JSONField(name = "platCategory")
    private Integer platformId; // 产商id
    @JSONField(name = "categoryId")
    private ProductCategory category; // 产品分类
    @JSONField(name = "gameCode")
    private String productCode; // 产品代码
    private String officialProductCode; // 官方产品代码
    @JSONField(name = "gameName")
    private String productName; // 产品名称（json）
    private Integer status; // 产品状态：1、正常；0、关闭
    private LocalDateTime addTime; // 添加时间
    private Integer sort; // 排序值

    /** 图片 **/
    @TableField(exist = false)
    private String iconPath;

    public void buildIconData(String serviceCode,String uploadPath,String baseFilePath) {
        if (this.iconPath == null) {
            if (!checkProductIcon(serviceCode, TenantUtils.getTenantId(),uploadPath,baseFilePath)) {
                checkProductIcon(serviceCode,1,uploadPath,baseFilePath);
            }
        }
    }

    private boolean checkProductIcon(String serviceCode,Integer tenantId,String uploadPath,String baseFilePath) {
        String iconName = serviceCode + "_" + tenantId + "_2_" + this.getPlatformId() + "_" + this.getProductCode();
        File imageFile = new File(baseFilePath + iconName + ".png");
        if (imageFile.exists()) {//存在则赋值，不存在则再判断webp后缀图片
            this.iconPath = uploadPath + iconName + ".png";
            return true;
        } else {
            imageFile = new File(baseFilePath + iconName + ".webp");
            if (imageFile.exists()) {
                this.iconPath =  uploadPath + iconName + ".webp";
                return true;
            } else {
                imageFile = new File(baseFilePath + iconName + ".avif");
                if (imageFile.exists()) {
                    this.iconPath = uploadPath + iconName + ".avif";
                    return true;
                } else {
                    imageFile = new File(baseFilePath + iconName + ".jpg");
                    if (imageFile.exists()) {
                        this.iconPath = uploadPath + iconName + ".jpg";
                        return true;
                    } else {
                        imageFile = new File(baseFilePath + iconName + ".jpeg");
                        if (imageFile.exists()) {
                            this.iconPath = uploadPath + iconName + ".jpeg";
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Getter
    public enum ProductCategory {
        VIDEO_SLOTS(1, "电子"),
        FISHING(2, "捕鱼"),
        LIVE_GAMES(3, "现场"),
        STREET(4, "街机"),
        ELE_LOTTERY(5, "电子彩票"),
        CHESS(6, "棋牌");

        @JsonValue
        @EnumValue
        private final int code;
        private final String desc;

        ProductCategory(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        @JsonCreator
        public static ProductCategory fromCode(int code) {
            for (ProductCategory modeEnum : ProductCategory.values()) {
                if (modeEnum.code == code) {
                    return modeEnum;
                }
            }
            throw new IllegalArgumentException("Invalid code: " + code);
        }
    }
}
