package com.console.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.payment.entity.CashOutRule;
import com.console.payment.mapper.CashOutRuleMapper;
import com.console.payment.service.CashOutRuleService;
import com.github.xiaolyuh.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CashOutRuleServiceImpl extends BaseServiceImpl<CashOutRuleMapper, CashOutRule> implements CashOutRuleService {
    @Override
    @Cacheable(value = CacheConstants.CASH_OUT_RULE, key = "#marketTeamId + ':' + #cashOutRuleType.getKey()")
    public CashOutRule getCashOutRule(Integer marketTeamId, CashOutRule.RuleType ruleType) {
        QueryWrapper<CashOutRule> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CashOutRule::getMarketTeamId,marketTeamId).eq(CashOutRule::getRuleType, ruleType);
        CashOutRule cashOutRule = this.getOne(queryWrapper);
        if (cashOutRule == null) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(CashOutRule::getMarketTeamId,0).eq(CashOutRule::getRuleType, ruleType);
            cashOutRule = this.getOne(queryWrapper);
        }
        return cashOutRule;
    }
}
