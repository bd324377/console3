package com.console.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.admin.dto.req.MediaReq;
import com.console.admin.entity.Media;
import com.console.admin.mapper.MediaMapper;
import com.console.admin.service.MediaService;
import com.console.core.entity.AdminProc;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.FieldUtils;
import com.console.framework.utils.RequestUtils;
import com.github.xiaolyuh.annotation.CacheEvict;
import com.github.xiaolyuh.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaServiceImpl extends BaseServiceImpl<MediaMapper, Media> implements MediaService {
    @Override
    @CacheEvict(value = CacheConstants.MEDIA_LIST,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #createMediaReq.marketSiteId")
    public boolean addMedia(MediaReq.CreateMediaReq createMediaReq) {
        Media media = createMediaReq.buildMedia();
        if (this.save(media)) {//保存成功后添加操作过程
            RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
            AdminProc adminProc = new AdminProc(requestBackendUser, AdminProc.ProcBelong.MEDIA, AdminProc.ProcType.ADD, media.getId(),null,media);
            adminProcMapper.insert(adminProc);
            return true;
        }
        return false;
    }

    @Override
    @CacheEvict(value = CacheConstants.MEDIA_LIST,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #delMediaReq.marketSiteId")
    public boolean delMedia(MediaReq.DelMediaReq delMediaReq) {
        if (this.removeById(delMediaReq.getMediaId())) {
            RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
            AdminProc adminProc = new AdminProc(requestBackendUser, AdminProc.ProcBelong.MEDIA, AdminProc.ProcType.DELETE, delMediaReq.getMediaId(),null,null);
            adminProcMapper.insert(adminProc);
            return true;
        }
        return false;
    }

    @Override
    @CacheEvict(value = CacheConstants.MEDIA_LIST,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #updateMediaReq.marketSiteId")
    public boolean updateMedia(String part, MediaReq.UpdateMediaReq updateMediaReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        QueryWrapper<Media> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", FieldUtils.convertToUnderscore(part)).lambda().eq(Media::getId,updateMediaReq.getId());
        Media media = this.getOne(queryWrapper);
        updateMediaReq.buildUpdateParam(part,media,requestBackendUser);
        if (this.update(updateMediaReq.getUpdateWrapper())) {
            adminProcMapper.insert(updateMediaReq.getAdminProc());
            return true;
        }
        return false;
    }

    @Override
    public IPage<Media> getMediaPage(Page<Media> page, MediaReq.SearchMediaReq searchMediaReq) {
        QueryWrapper<Media> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Media::getMarketSiteId,searchMediaReq.getMarketSiteId())
                .eq(searchMediaReq.getTitle()  != null, Media::getTitle, searchMediaReq.getTitle())
                .eq(searchMediaReq.getMediaType() != null, Media::getMediaType,searchMediaReq.getMediaType())
                .eq(searchMediaReq.getContent() != null, Media::getContent, searchMediaReq.getContent());
        return this.page(page, queryWrapper);
    }

    @Override
    @Cacheable(value = CacheConstants.MEDIA_LIST,key = "#tenantId + ':' + #marketSiteId")
    public List<Media> getMediaList(Integer tenantId, Integer marketSiteId) {
        QueryWrapper<Media> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("pic_path,media_type,route_path").lambda().eq(Media::getMarketSiteId,marketSiteId).orderByDesc(Media::getSort);
        return this.list(queryWrapper);
    }
}
