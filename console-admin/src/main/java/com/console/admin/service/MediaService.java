package com.console.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.console.admin.dto.req.MediaReq;
import com.console.admin.entity.Media;

import java.util.List;

public interface MediaService extends IService<Media> {
    boolean addMedia(MediaReq.CreateMediaReq createMediaReq);//新增媒体信息
    boolean delMedia(MediaReq.DelMediaReq delMediaReq);//根据媒体ID删除媒体信息
    boolean updateMedia(String part, MediaReq.UpdateMediaReq updateMediaReq);//修改媒体信息
    IPage<Media> getMediaPage(Page<Media> page, MediaReq.SearchMediaReq searchMediaReq);//分页获取媒体信息
    List<Media> getMediaList(Integer tenantId, Integer marketSiteId);
}
