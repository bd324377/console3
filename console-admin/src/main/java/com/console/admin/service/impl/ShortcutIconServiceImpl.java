package com.console.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.admin.model.bo.ShortcutIconDTO;
import com.console.admin.model.req.ShortcutIconReq;
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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

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
    public List<ShortcutIconDTO> getShortcutIconList(Integer tenantId, Integer marketSiteId) {
        QueryWrapper<ShortcutIcon> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("icon_type,show_img,route_path").lambda().eq(ShortcutIcon::getMarketSiteId,marketSiteId).orderByDesc(ShortcutIcon::getSort);
        List<ShortcutIcon> shortcutIconList = this.list(queryWrapper);
        if (ObjectUtils.isEmpty(shortcutIconList)) return null;
        return shortcutIconList.stream().map(item ->{
            ShortcutIconDTO shortcutIconDTO = new ShortcutIconDTO();
            BeanUtils.copyProperties(item,shortcutIconDTO);
            return shortcutIconDTO;
        }).toList();
    }
}
