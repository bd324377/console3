package com.console.product.service.impl;

import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.product.entity.PlatformBelongRule;
import com.console.product.mapper.PlatformBelongRuleMapper;
import com.console.product.service.PlatformBelongRuleService;
import com.github.xiaolyuh.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PlatformBelongRuleServiceImpl extends BaseServiceImpl<PlatformBelongRuleMapper, PlatformBelongRule> implements PlatformBelongRuleService {
    @Override
    @Cacheable(value = CacheConstants.PLATFORM_BELONG_RULE,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #ruleId")
    public PlatformBelongRule getPlatformBelongRuleById(Integer ruleId) {
        return this.getById(ruleId);
    }
}
