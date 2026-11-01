package com.console.user.service.impl;

import com.console.user.entity.OnlineReport;
import com.console.user.mapper.OnlineReportMapper;
import com.console.user.model.OnlineReportService;
import com.console.core.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OnlineReportServiceImpl extends BaseServiceImpl<OnlineReportMapper, OnlineReport> implements OnlineReportService {
}
