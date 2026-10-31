package com.console.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.product.entity.PlatformBelongRuleItem;
import com.console.product.mapper.PlatformBelongRuleItemMapper;
import com.console.product.service.PlatformBelongRuleItemService;
import com.github.xiaolyuh.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PlatformBelongRuleItemServiceImpl extends BaseServiceImpl<PlatformBelongRuleItemMapper, PlatformBelongRuleItem> implements PlatformBelongRuleItemService {

    @Override
    @Cacheable(value = CacheConstants.PLATFORM_RULE_ITEM,
            key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #ruleId + ':' + #platformId")
    public PlatformBelongRuleItem getPlatformBelongRuleItem(Integer ruleId,Integer platformId) {
        QueryWrapper<PlatformBelongRuleItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(PlatformBelongRuleItem::getRuleId,ruleId)
                .eq(PlatformBelongRuleItem::getPlatformId,platformId);
        return this.getOne(queryWrapper);
    }
}
