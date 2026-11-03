package com.console.admin.model.bo;

import com.console.admin.entity.ShortcutIcon;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class ShortcutIconDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private ShortcutIcon.IconType iconType;//快捷键类型类型
    private String showImg;//展示图片
    private String routePath;//跳转地址
}
