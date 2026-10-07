package com.console.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.framework.domain.Result;
import com.console.user.model.dto.req.AgentReq;
import com.console.user.entity.Agent;
import com.console.user.service.AgentService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 代理表 前端控制器
 * </p>
 *
 * @author aha
 * @since 2024-09-24
 */
@RestController
@RequestMapping("/agent")
public class AgentController {
    @Resource
    private AgentService agentService;

    @PostMapping
    public Result addAgent(@RequestBody Agent agent) {
        return agentService.addAgent(agent) ? Result.success() : Result.failed();
    }

    /**
     * 根据代理等级主键删除代理等级信息
     * @param agentId 代理等级主键ID
     */
    @DeleteMapping("/{agentId}")
    public Result delAgentById(@PathVariable(value = "agentId") Long agentId) {
        return agentService.delAgentById(agentId) ? Result.success() : Result.failed();
    }

    /**
     * 更新代理等级信息
     * @param part          待更新字段名
     * @param updateAgentReq 待更新代理等级信息
     */
    @PutMapping("/{part}")
    public Result updateAgent(@PathVariable(value = "part") String part, @RequestBody AgentReq.UpdateAgentReq updateAgentReq) {
        return agentService.updateAgent(part, updateAgentReq) ? Result.success() : Result.failed();
    }

    /**
     * 分页获取代理等级列表
     * @param page  分页信息
     * @param agent 代理等级查询条件
     */
    @GetMapping
    public Result getAgentPage(Page<Agent> page, Agent agent) {
        return Result.success(agentService.getAgentPage(page,agent));
    }

    /**
     * 获取所有代理等级列表
     */
    @GetMapping("/list")
    public Result getAgentList() {
        return Result.success(agentService.getAgentList());
    }
}
