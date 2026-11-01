package com.console.admin.dto.req;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.console.admin.entity.ShortcutIcon;
import com.console.core.entity.AdminProc;
import com.console.core.model.req.BaseUpdateReq;
import com.console.framework.domain.IUpdateField;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;

public class ShortcutIconReq {
    @Getter
    @Setter
    public static class CreateShortIconReq {
        @NotNull
        private Integer marketSiteId;//站点ID
        private String iconName;
        @NotNull
        private ShortcutIcon.IconType iconType;//快捷键类型类型
        private Integer showStatus;
        private Integer sort;//排序号
        @NotNull
        private String showImg;//展示图片
        private String routePath;//跳转地址

        public ShortcutIcon buildShortcutIcon() {
            ShortcutIcon shortcutIcon = new ShortcutIcon();
            BeanUtils.copyProperties(this,shortcutIcon);
            return shortcutIcon;
        }
    }

    @Getter
    @Setter
    public static class DelShortIconReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @NotNull
        private Integer marketSiteId;//站点ID
        @NotNull
        private Integer shortcutIconId;

    }

    @Getter
    @Setter
    public static class UpdateShortIconReq extends BaseUpdateReq<ShortcutIcon> {
        @NotNull
        private Integer marketSiteId;
        private Integer showStatus;
        private Integer sort;//排序号
        private String iconName;
        private String showImg;//展示图片
        private String routePath;//跳转地址

        @Getter
        public enum UpdateField implements IUpdateField<ShortcutIcon> {
            SHOW_STATUS("showStatus",ShortcutIcon::getShowStatus,"shortcutIcon.proc.update.showStatus"),
            SORT("sort",ShortcutIcon::getSort,"shortcutIcon.proc.update.showStatus"),
            ICON_NAME("iconName",ShortcutIcon::getIconName,"shortcutIcon.proc.update.showStatus"),
            SHOW_IMG("showImg",ShortcutIcon::getShowImg,"shortcutIcon.proc.update.showStatus"),
            ROUTE_PATH("routePath",ShortcutIcon::getRoutePath,"shortcutIcon.proc.update.showStatus");

            private final String fieldName;
            private final SFunction<ShortcutIcon, Object> function;
            private final String descriptionKey;

            UpdateField(String fieldName, SFunction<ShortcutIcon, Object> function, String descriptionKey) {
                this.fieldName = fieldName;
                this.function = function;
                this.descriptionKey = descriptionKey;
            }
        }
        @Override
        protected SFunction<ShortcutIcon, ?> idFunction() {
            return ShortcutIcon::getId;
        }

        @Override
        protected AdminProc.ProcBelong procBelong() {
            return AdminProc.ProcBelong.SHORTCUT_ICON;
        }

        @Override
        protected IUpdateField<ShortcutIcon> parseField(String fieldName) {
            return IUpdateField.resolve(UpdateField.class,fieldName);
        }
    }

    @Getter
    @Setter
    public static class SearchShortIconReq {
        @NotNull
        private Integer marketSiteId;//站点ID
        @NotNull
        private ShortcutIcon.IconType iconType;//快捷键类型类型
        private Integer showStatus;
        private String routePath;//跳转地址
    }
}
