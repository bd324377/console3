package com.console.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.console.user.entity.Avatar;
import com.console.user.model.dto.req.AvatarReq;

import java.util.List;

public interface AvatarService extends IService<Avatar> {
    boolean addAvatar(Avatar avatar);
    boolean delAvatar(AvatarReq.DelAvatarReq delAvatarReq);
    boolean updateAvatar(String field, AvatarReq.UpdateAvatarReq updateAvatarReq);
    Object getAvatarPage(Page<Avatar> page, Avatar avatar);
    List<Avatar> getAvatarList(Integer marketSiteId, Integer sex);
}
