package com.console.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.core.entity.AdminProc;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.framework.domain.BusinessException;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.FieldUtils;
import com.console.framework.utils.RedisUtils;
import com.console.framework.utils.RequestUtils;
import com.console.framework.utils.TenantUtils;
import com.console.user.entity.Avatar;
import com.console.user.mapper.AvatarMapper;
import com.console.user.model.dto.req.AvatarReq;
import com.console.user.service.AvatarService;
import com.github.xiaolyuh.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class AvatarServiceImpl extends BaseServiceImpl<AvatarMapper,Avatar> implements AvatarService {
    @Override
    public boolean addAvatar(Avatar avatar) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        if (this.save(avatar)) {
            clearAvatarCache(requestBackendUser,avatar.getMarketSiteId());
            AdminProc adminProc = new AdminProc(requestBackendUser, AdminProc.ProcBelong.AVATAR, AdminProc.ProcType.ADD,avatar.getId(),null,avatar);
            adminProcMapper.insert(adminProc);
            return true;
        }
        return false;
    }

    @Override
    public boolean delAvatar(AvatarReq.DelAvatarReq delAvatarReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        Avatar avatar = this.getById(delAvatarReq.getAvatarId());
        if (avatar == null) {
            throw new BusinessException(500,"删除失败，未在平台找到该头像");
        }
        if (this.removeById(delAvatarReq.getAvatarId())) {
            clearAvatarCache(requestBackendUser,avatar.getMarketSiteId());
            AdminProc adminProc = new AdminProc(requestBackendUser, AdminProc.ProcBelong.AVATAR, AdminProc.ProcType.DELETE,avatar.getId(),null,avatar);
            adminProcMapper.insert(adminProc);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateAvatar(String field, AvatarReq.UpdateAvatarReq updateAvatarReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        QueryWrapper<Avatar> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,market_site_id", FieldUtils.convertToUnderscore(field)).lambda().eq(Avatar::getId,updateAvatarReq.getId());
        Avatar avatar = this.getOne(queryWrapper);
        updateAvatarReq.buildUpdateParam(field,avatar,requestBackendUser);
        if (this.update(updateAvatarReq.getUpdateWrapper())) {
            adminProcMapper.insert(updateAvatarReq.getAdminProc());
            clearAvatarCache(requestBackendUser,avatar.getMarketSiteId());
            return true;
        }
        return false;
    }

    @Override
    public Object getAvatarPage(Page<Avatar> page, Avatar avatar) {
        QueryWrapper<Avatar> avatarQueryWrapper = new QueryWrapper<>();
        avatarQueryWrapper.lambda()
                .likeRight(StringUtils.hasText(avatar.getAvatarName()),Avatar::getAvatarName,avatar.getAvatarName())
                .eq(avatar.getSex() != null,Avatar::getSex,avatar.getSex());
        return this.page(page,avatarQueryWrapper);
    }

    /**
     * 获取头像列表
     */
    @Override
    @Cacheable(value = CacheConstants.AVATAR,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #marketSiteId + ':' + #sex")
    public List<Avatar> getAvatarList(Integer marketSiteId,Integer sex) {
        QueryWrapper<Avatar> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Avatar::getSex,sex).orderByDesc(Avatar::getSort);
        List<Avatar> avatarList = this.list(queryWrapper);
        if (ObjectUtils.isEmpty(avatarList)) {
            return getAvatarList(sex);
        }
        return avatarList;
    }

    @Cacheable(value = CacheConstants.AVATAR,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #sex")
    public List<Avatar> getAvatarList(Integer sex) {
        QueryWrapper<Avatar> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Avatar::getSex,sex).isNull(Avatar::getMarketSiteId).orderByDesc(Avatar::getSort);
        return this.list(queryWrapper);
    }

    private void clearAvatarCache(RequestBackendUser requestBackendUser, Integer marketSiteId) {
        String redisKeyPrefix = CacheConstants.AVATAR + ":" + requestBackendUser.getTenantId();
        if (marketSiteId == null) {
            redisKeyPrefix = redisKeyPrefix + ":*";
        } else {
            redisKeyPrefix = redisKeyPrefix + ":" + marketSiteId + ":*";
        }
        RedisUtils.delKeysByPattern(redisKeyPrefix);
    }
}
