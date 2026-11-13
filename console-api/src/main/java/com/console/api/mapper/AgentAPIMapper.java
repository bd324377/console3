package com.console.api.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.api.dto.req.UserReq;
import com.console.api.dto.res.AgentPerformanceRes;
import com.console.api.dto.res.SubTeamRechargeReport;
import com.console.api.dto.res.SubUserRes;
import com.console.user.entity.User;
import org.apache.ibatis.annotations.Param;

public interface AgentAPIMapper {
    SubTeamRechargeReport selectSubTeamRechargeReport( @Param("param") UserReq.SearchSubUserReq searchSubUserReq);
    IPage<SubUserRes> selectSubUserPage(Page<User> page, @Param("param") UserReq.SearchSubUserReq searchSubUserReq);
    AgentPerformanceRes selectAgentPerformanceReport(@Param("param") UserReq.SearchAgentPerformanceReq searchAgentPerformanceReq);
}
