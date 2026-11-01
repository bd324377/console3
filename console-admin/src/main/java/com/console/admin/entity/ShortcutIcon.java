package com.console.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@TableName("s_shortcut_icon")
public class ShortcutIcon implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private Integer marketSiteId;//站点ID
    private String iconName;
    private IconType iconType;//快捷键类型类型
    private Integer showStatus;
    private Integer sort;//排序号
    private String showImg;//展示图片
    private String routePath;//跳转地址

    @Getter
    public enum IconType {
        UNKNOWN_TYPE(0,"未知类型"),
        RIGHT(1,"右侧快捷键"),
        LEFT(2,"左侧快捷栏"),
        BOTTOM(3,"底部快捷键");

        @JsonValue
        @EnumValue
        private final Integer key;
        private final String  value;

        IconType(int key, String value) {
            this.key   = key;
            this.value = value;
        }
    }
}
