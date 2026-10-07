package com.console.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.console.user.model.dto.req.AgentReq;
import com.console.user.entity.Agent;

import java.util.List;

public interface AgentService extends IService<Agent> {
    boolean addAgent(Agent agent);//新增代理等级信息
    boolean delAgentById(Long agentId);//通过代理主键ID删除代理等级信息
    boolean updateAgent(String part, AgentReq.UpdateAgentReq updateAgentReq);//更新代理等级信息
    IPage<Agent> getAgentPage(Page<Agent> page, Agent agent);//分页获取代理等级信息
    Integer getInitialAgentId();//获取初始代理等级信息
    Agent getAgentById(Long agentId);//通过代理ID获取代理等级信息
    List<Agent> getAgentList();//获取所有代理等级列表
}
