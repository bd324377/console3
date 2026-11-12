package com.console.api.controller;

import com.console.api.dto.req.UserReq;
import com.console.api.service.AgentAPIService;
import com.console.framework.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgentAPI {
    @Resource
    private AgentAPIService agentAPIService;

    /**
     * 获取下级用户信息列表
     */
    @PostMapping("/subUserPage")
    public Result getSubUserPage(@RequestBody UserReq.SearchSubUserReq searchSubUserReq) {
        return Result.success(agentAPIService.getSubUserPage(searchSubUserReq));
    }
}
