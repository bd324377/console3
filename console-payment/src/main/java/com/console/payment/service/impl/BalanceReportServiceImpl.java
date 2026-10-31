package com.console.payment.service.impl;

import com.console.core.service.impl.BaseServiceImpl;
import com.console.payment.entity.BalanceReport;
import com.console.payment.mapper.BalanceReportMapper;
import com.console.payment.service.BalanceReportService;
import org.springframework.stereotype.Service;

@Service
public class BalanceReportServiceImpl extends BaseServiceImpl<BalanceReportMapper, BalanceReport> implements BalanceReportService {
}
