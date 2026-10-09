package com.console.payment.service.impl;

import com.console.core.service.impl.BaseServiceImpl;
import com.console.payment.entity.CashOutRule;
import com.console.payment.mapper.CashOutRuleMapper;
import com.console.payment.service.CashOutRuleService;
import org.springframework.stereotype.Service;

@Service
public class CashOutRuleServiceImpl extends BaseServiceImpl<CashOutRuleMapper, CashOutRule> implements CashOutRuleService {
}
