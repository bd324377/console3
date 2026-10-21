package com.console.user.service.impl;

import com.console.core.service.impl.BaseServiceImpl;
import com.console.user.entity.UserReport;
import com.console.user.mapper.UserReportMapper;
import com.console.user.service.UserReportService;
import org.springframework.stereotype.Service;

@Service
public class UserReportServiceImpl extends BaseServiceImpl<UserReportMapper, UserReport> implements UserReportService {
}
