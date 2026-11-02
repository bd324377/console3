package com.console.api.dto.res;

import com.console.admin.entity.LayoutConfig;
import com.console.admin.entity.Media;
import com.console.admin.entity.ShortcutIcon;
import com.console.product.entity.ProductType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class LoadLayoutRes {
    private LayoutConfig layoutConfig;
    private List<Media>  bannerList;
    private List<Media>  popUpBannerList;
    private List<Media>  adverBannerList;
    private List<ShortcutIcon> rightShortcutIconList;
    private List<ShortcutIcon> leftShortcutIconList;
    private List<ShortcutIcon> bottomShortcutIconList;
    private List<ProductType>  productTypeList;

    public LoadLayoutRes(LayoutConfig layoutConfig,List<Media> mediaList,List<ShortcutIcon> shortcutIconList,List<ProductType> productTypeList) {
        this.layoutConfig = layoutConfig;
        this.productTypeList = productTypeList;
        if (!ObjectUtils.isEmpty(mediaList)) {
            Map<Media.MediaType, List<Media>> mediaTypeMap = mediaList.stream().collect(Collectors.groupingBy(Media::getMediaType));
            this.bannerList = mediaTypeMap.get(Media.MediaType.BANNER);
            this.popUpBannerList = mediaTypeMap.get(Media.MediaType.POP_UP);
            this.adverBannerList = mediaTypeMap.get(Media.MediaType.ADVERT);
        }
        if (!ObjectUtils.isEmpty(shortcutIconList)) {
            Map<ShortcutIcon.IconType,List<ShortcutIcon>> iconTypeMap = shortcutIconList.stream().collect(Collectors.groupingBy(ShortcutIcon::getIconType));
            this.rightShortcutIconList = iconTypeMap.get(ShortcutIcon.IconType.RIGHT);
            this.leftShortcutIconList = iconTypeMap.get(ShortcutIcon.IconType.LEFT);
            this.bottomShortcutIconList = iconTypeMap.get(ShortcutIcon.IconType.BOTTOM);
        }
    }
}
