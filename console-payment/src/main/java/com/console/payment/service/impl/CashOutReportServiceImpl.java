package com.console.payment.service.impl;

import com.console.core.service.impl.BaseServiceImpl;
import com.console.payment.entity.CashOutReport;
import com.console.payment.mapper.CashOutReportMapper;
import com.console.payment.service.CashOutReportService;
import org.springframework.stereotype.Service;

@Service
public class CashOutReportServiceImpl extends BaseServiceImpl<CashOutReportMapper, CashOutReport> implements CashOutReportService {
}
