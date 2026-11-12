package com.console.payment.service.impl;

import com.console.core.service.impl.BaseServiceImpl;
import com.console.payment.entity.WalletReport;
import com.console.payment.mapper.WalletReportMapper;
import com.console.payment.service.WalletReportService;
import org.springframework.stereotype.Service;

@Service
public class WalletReportServiceImpl extends BaseServiceImpl<WalletReportMapper, WalletReport> implements WalletReportService {
}
