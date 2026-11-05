package com.console.api.dto.res;

import com.console.admin.entity.Media;
import com.console.admin.entity.ShortcutIcon;
import com.console.admin.model.bo.LayoutConfigDTO;
import com.console.admin.model.bo.MediaDTO;
import com.console.admin.model.bo.ShortcutIconDTO;
import com.console.product.entity.ProductType;
import com.console.product.model.dto.ProductTypeDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class LoadLayoutRes {
    private String  timeZone;
    private LayoutConfigDTO layoutConfig;
    private List<MediaDTO>  bannerList;
    private List<MediaDTO>  popUpBannerList;
    private List<MediaDTO>  adverBannerList;
    private List<ShortcutIconDTO> rightShortcutIconList;
    private List<ShortcutIconDTO> leftShortcutIconList;
    private List<ShortcutIconDTO> bottomShortcutIconList;

    public LoadLayoutRes(String timeZone,
                         LayoutConfigDTO layoutConfig,
                         List<MediaDTO> mediaList,
                         List<ShortcutIconDTO> shortcutIconList) {
        this.timeZone = timeZone;
        this.layoutConfig = layoutConfig;
        if (!ObjectUtils.isEmpty(mediaList)) {
            Map<Media.MediaType, List<MediaDTO>> mediaTypeMap = mediaList.stream().collect(Collectors.groupingBy(MediaDTO::getMediaType));
            this.bannerList = mediaTypeMap.get(Media.MediaType.BANNER);
            this.popUpBannerList = mediaTypeMap.get(Media.MediaType.POP_UP);
            this.adverBannerList = mediaTypeMap.get(Media.MediaType.ADVERT);
        }
        if (!ObjectUtils.isEmpty(shortcutIconList)) {
            Map<ShortcutIcon.IconType,List<ShortcutIconDTO>> iconTypeMap = shortcutIconList.stream().collect(Collectors.groupingBy(ShortcutIconDTO::getIconType));
            this.rightShortcutIconList = iconTypeMap.get(ShortcutIcon.IconType.RIGHT);
            this.leftShortcutIconList = iconTypeMap.get(ShortcutIcon.IconType.LEFT);
            this.bottomShortcutIconList = iconTypeMap.get(ShortcutIcon.IconType.BOTTOM);
        }
    }
}
