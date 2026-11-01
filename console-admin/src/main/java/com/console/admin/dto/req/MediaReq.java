package com.console.admin.dto.req;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.console.admin.entity.Media;
import com.console.core.entity.AdminProc;
import com.console.core.model.req.BaseUpdateReq;
import com.console.framework.constants.ValidationGroup;
import com.console.framework.domain.IUpdateField;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;

public class MediaReq {

    @Getter
    @Setter
    public static class CreateMediaReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @NotNull
        private Integer marketSiteId;
        @NotNull
        private String picPath;
        private String title;
        private String content;
        @NotNull
        private Media.MediaType mediaType;//类型：1、轮播图；2、弹出框；3、广告位
        private String routePath;
        private Integer sort;
        private Media.PopUpRule popUpRule;//弹框规则

        public Media buildMedia() {
            Media media = new Media();
            BeanUtils.copyProperties(this,media);
            return media;
        }
    }

    @Getter
    @Setter
    public static class UpdateMediaReq extends BaseUpdateReq<Media> {
        @NotNull
        private Integer marketSiteId;
        private String picPath;
        private String title;
        private String content;
        private String routePath;
        private Integer sort;
        private Media.PopUpRule popUpRule;//弹框规则

        @Getter
        public enum UpdateField implements IUpdateField<Media> {
            PIC_PATH("picPath",Media::getPicPath,"media.proc.update.picPath"),
            TITLE("title",Media::getTitle,"media.proc.update.title"),
            CONTENT("content",Media::getContent,"media.proc.update.content"),
            ROUTE_PATH("routePath",Media::getRoutePath,"media.proc.update.routePath"),
            SORT("sort",Media::getSort,"media.proc.update.sort"),
            POP_UP_RULE("popUpRule",Media::getPopUpRule,"media.proc.update.popUpRule");
            private final String fieldName;
            private final SFunction<Media, Object> function;
            private final String descriptionKey;

            UpdateField(String fieldName, SFunction<Media, Object> function, String descriptionKey) {
                this.fieldName = fieldName;
                this.function = function;
                this.descriptionKey = descriptionKey;
            }
        }
        @Override
        protected SFunction<Media, ?> idFunction() {
            return Media::getId;
        }

        @Override
        protected AdminProc.ProcBelong procBelong() {
            return AdminProc.ProcBelong.MEDIA;
        }

        @Override
        protected IUpdateField<Media> parseField(String fieldName) {
            return IUpdateField.resolve(UpdateField.class,fieldName);
        }
    }

    @Getter
    @Setter
    public static class SearchMediaReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @NotNull
        private Integer marketSiteId;//站点ID
        private String title;
        private String content;
        private Media.MediaType mediaType;//类型：1、轮播图；2、弹出框；3、广告位
    }

    @Getter
    @Setter
    public static class DelMediaReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @NotNull
        private Integer marketSiteId;//站点ID
        @NotNull
        private Integer mediaId;
    }
}
