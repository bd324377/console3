package com.console.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.product.entity.PlatformBelongRuleItem;

public interface PlatformBelongRuleItemService extends IService<PlatformBelongRuleItem> {
    PlatformBelongRuleItem getPlatformBelongRuleItem(Integer ruleId,Integer platformId);
}
