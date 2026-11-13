package com.console.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.console.api.dto.req.UserReq;
import com.console.api.dto.res.OrderRakeBackIntroductionRes;
import com.console.api.dto.res.SubPerformancePageRes;
import com.console.api.dto.res.SubPerformanceReport;
import com.console.api.dto.res.SubUserPageRes;

public interface AgentAPIService {
    SubUserPageRes getSubUserPage(UserReq.SearchSubUserReq searchSubUserReq);
    Object getAgentPerformanceReport(UserReq.SearchAgentPerformanceReq searchAgentPerformanceReq);
    OrderRakeBackIntroductionRes getOrderRakeBackIntroduction();
    SubPerformancePageRes getSubPerformanceReportPage(UserReq.SearchAgentPerformanceReq searchAgentPerformanceReq);
}
