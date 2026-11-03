package com.console.admin.model.bo;

import com.console.admin.entity.Media;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class MediaDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String picPath;
    private Media.MediaType mediaType;//类型：1、轮播图；2、弹出框；3、广告位
    private String routePath;
}
