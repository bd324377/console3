package com.console.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.admin.dto.req.ShortcutIconReq;
import com.console.admin.entity.Media;
import com.console.admin.entity.ShortcutIcon;
import com.console.admin.mapper.ShortcutIconMapper;
import com.console.admin.service.ShortcutIconService;
import com.console.core.entity.AdminProc;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.FieldUtils;
import com.console.framework.utils.RequestUtils;
import com.github.xiaolyuh.annotation.CacheEvict;
import com.github.xiaolyuh.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class ShortcutIconServiceImpl extends BaseServiceImpl<ShortcutIconMapper, ShortcutIcon> implements ShortcutIconService {
    @Override
    @CacheEvict(value = CacheConstants.SHORTCUT_ICON_LIST,key = "#tenantId + ':' + #marketSiteId")
    public boolean addShortcutIcon(ShortcutIconReq.CreateShortIconReq createShortIconReq) {
        ShortcutIcon shortcutIcon = createShortIconReq.buildShortcutIcon();
        if (this.save(shortcutIcon)) {
            RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
            AdminProc adminProc = new AdminProc(requestBackendUser, AdminProc.ProcBelong.SHORTCUT_ICON, AdminProc.ProcType.ADD, shortcutIcon.getId(),null,shortcutIcon);
            adminProcMapper.insert(adminProc);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.SHORTCUT_ICON_LIST,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #delShortIconReq.marketSiteId")
    public boolean delShortcutIcon(ShortcutIconReq.DelShortIconReq delShortIconReq) {
        if (this.removeById(delShortIconReq.getShortcutIconId())) {
            RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
            AdminProc adminProc = new AdminProc(requestBackendUser, AdminProc.ProcBelong.SHORTCUT_ICON, AdminProc.ProcType.DELETE, delShortIconReq.getShortcutIconId(), null, null);
            adminProcMapper.insert(adminProc);
            return true;
        }
        return false;
    }

    @Override
    @CacheEvict(value = CacheConstants.SHORTCUT_ICON_LIST,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #updateShortIconReq.marketSiteId")
    public boolean updateShortcutIcon(String part, ShortcutIconReq.UpdateShortIconReq updateShortIconReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        QueryWrapper<ShortcutIcon> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", FieldUtils.convertToUnderscore(part)).lambda().eq(ShortcutIcon::getId,updateShortIconReq.getId());
        ShortcutIcon shortcutIcon = this.getOne(queryWrapper);
        updateShortIconReq.buildUpdateParam(part,shortcutIcon,requestBackendUser);
        if (this.update(updateShortIconReq.getUpdateWrapper())) {
            adminProcMapper.insert(updateShortIconReq.getAdminProc());
            return true;
        }
        return false;
    }

    @Override
    public IPage<ShortcutIcon> getShortcutIconPage(Page<ShortcutIcon> page, ShortcutIconReq.SearchShortIconReq searchShortIconReq) {
        QueryWrapper<ShortcutIcon> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ShortcutIcon::getIconType,searchShortIconReq.getIconType())
                .eq(ShortcutIcon::getMarketSiteId,searchShortIconReq.getMarketSiteId());
        return this.page(page,queryWrapper);
    }

    @Override
    @Cacheable(value = CacheConstants.SHORTCUT_ICON_LIST,key = "#tenantId + ':' + #marketSiteId")
    public List<ShortcutIcon> getShortcutIconList(Integer tenantId, Integer marketSiteId) {
        QueryWrapper<ShortcutIcon> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ShortcutIcon::getMarketSiteId,marketSiteId).orderByDesc(ShortcutIcon::getSort);
        return this.list(queryWrapper);
    }
}
