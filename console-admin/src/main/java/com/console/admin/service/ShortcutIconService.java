package com.console.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.console.admin.model.bo.ShortcutIconDTO;
import com.console.admin.model.req.ShortcutIconReq;
import com.console.admin.entity.ShortcutIcon;

import java.util.List;

public interface ShortcutIconService extends IService<ShortcutIcon> {
    boolean addShortcutIcon(ShortcutIconReq.CreateShortIconReq createShortIconReq);
    boolean delShortcutIcon(ShortcutIconReq.DelShortIconReq delShortIconReq);
    boolean updateShortcutIcon(String part, ShortcutIconReq.UpdateShortIconReq updateShortIconReq);
    IPage<ShortcutIcon> getShortcutIconPage(Page<ShortcutIcon> page, ShortcutIconReq.SearchShortIconReq searchShortIconReq);
    List<ShortcutIconDTO> getShortcutIconList(Integer tenantId, Integer marketSiteId);
}
