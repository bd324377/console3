package com.console.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@TableName(value = "s_media",autoResultMap = true)
public class Media implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * banner图主键id
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private Integer marketSiteId;
    private String picPath;
    private String title;
    private String content;
    private MediaType mediaType;//类型：1、轮播图；2、弹出框；3、广告位
    private String routePath;
    private Integer sort;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private PopUpRule popUpRule;//弹框规则

    @Getter
    public enum MediaType {
        UNKNOWN_TYPE(0,"未知类型"),
        BANNER(1,"轮播图"),
        POP_UP(2,"弹框"),
        ADVERT(3,"广告栏");

        @JsonValue
        @EnumValue
        private final Integer key;
        private final String  value;

        MediaType(int key, String value) {
            this.key   = key;
            this.value = value;
        }
    }

    /**
     * 弹框规则
     */
    @Getter
    @Setter
    public static class PopUpRule {
        private Integer popUpTimes;       //弹窗次数 1、只弹一次；2、每次进入指定页面都弹
        private Long    popUpPosition;    //弹窗位置 0、首页;非0、产商ID对应产商产品详情页面
        private Integer closeRule;        //关闭规则 1、可关闭；2、不可关闭
        private String  popUpPositionName;//弹窗位置名称
    }
}
