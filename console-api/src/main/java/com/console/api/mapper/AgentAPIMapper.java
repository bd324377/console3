package com.console.api.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.api.dto.req.UserReq;
import com.console.api.dto.res.SubTeamRechargeReport;
import com.console.user.entity.User;
import org.apache.ibatis.annotations.Param;

public interface AgentAPIMapper {
    SubTeamRechargeReport selectSubTeamRechargeReport( @Param("param") UserReq.SearchSubUserReq searchSubUserReq);
    IPage<User> selectSubUserPage(Page<User> page,  @Param("param") UserReq.SearchSubUserReq searchSubUserReq);
}
