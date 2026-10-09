package com.console.payment.service.impl;

import com.console.core.service.impl.BaseServiceImpl;
import com.console.payment.entity.PaymentReport;
import com.console.payment.mapper.PaymentReportMapper;
import com.console.payment.service.PaymentReportService;
import org.springframework.stereotype.Service;

@Service
public class PaymentReportServiceImpl extends BaseServiceImpl<PaymentReportMapper, PaymentReport> implements PaymentReportService {
}
