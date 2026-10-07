package com.console.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.framework.domain.BusinessException;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.FieldUtils;
import com.console.framework.utils.I18nUtil;
import com.console.framework.utils.RequestUtils;
import com.console.user.model.dto.req.AgentReq;
import com.console.user.entity.Agent;
import com.console.user.entity.User;
import com.console.user.mapper.AgentMapper;
import com.console.user.mapper.UserMapper;
import com.console.user.service.AgentService;
import com.github.xiaolyuh.annotation.CacheEvict;
import com.github.xiaolyuh.annotation.Cacheable;
import com.github.xiaolyuh.annotation.FirstCache;
import com.github.xiaolyuh.annotation.SecondaryCache;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class AgentServiceImpl extends ServiceImpl<AgentMapper, Agent> implements AgentService {
//    @Resource
//    private ProcMerger procMerger;
    
    @Resource
    private UserMapper userMapper;

    /**
     * 新增代理等级信息
     * @param agent 代理等级信息
     */
    @Override
    public boolean addAgent(Agent agent) {
        if (this.save(agent)) {//新增等级成功，添加新增代理等级操作日志
            RequestBackendUser admin = RequestUtils.getUser(RequestBackendUser.class);
//            AdminProc adminProc = new AdminProc(admin, AdminProc.ProcBelong.AGENT_LEVEL_MANAGE, AdminProc.ProcType.ADD_AGENT,agent.getId());
//            procMerger.submitAdminProc(adminProc);
            return true;
        }
        return false;
    }

    /**
     * 根据代理等级ID删除代理等级信息
     * @param agentId   代理等级ID
     */
    @Override
    public boolean delAgentById(Long agentId) {
        Agent agent = getAgentById(agentId);
        if (agent == null) {
            throw new BusinessException(500, I18nUtil.getI8nMsg("agent.error.absent",RequestUtils.getLang()));
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getAgentId,agentId);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(500, I18nUtil.getI8nMsg("agent.error.haveUserInThisLevel",RequestUtils.getLang()));
        }
        return this.removeById(agentId);
    }

    /**
     * 更新代理等级信息
     * @param part              待更新字段
     * @param updateAgentReq    待更新信息
     */
    @Override
    @CacheEvict(value = CacheConstants.INITIAL_AGENT,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #updateAgentDTO.id")
    public boolean updateAgent(String part, AgentReq.UpdateAgentReq updateAgentReq) {
        QueryWrapper<Agent> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", FieldUtils.convertToUnderscore(part)).lambda().eq(Agent::getId, updateAgentReq.getId());
        Agent agent = this.getOne(queryWrapper);
        updateAgentReq.buildUpdateParam(part,agent);
        if (updateAgentReq.getUpdateWrapper() != null && this.update(updateAgentReq.getUpdateWrapper())) {
//            procMerger.submitAdminProc(updateAgentDTO.getAdminProc());
            return true;
        }
        return false;
    }

    /**
     * 分页获取代理等级信息列表
     * @param page      分页条件
     * @param agent     查询条件
     */
    @Override
    public IPage<Agent> getAgentPage(Page<Agent> page, Agent agent) {
        QueryWrapper<Agent> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(agent.getLevel() != null, Agent::getLevel, agent.getLevel())
                .likeRight(StringUtils.hasText(agent.getAgentName()), Agent::getAgentName, agent.getAgentName())
                .orderByAsc(Agent::getLevel);
        return this.page(page,queryWrapper);
    }

    /**
     * 获取初始代理等级信息
     */
    @Override
    @Cacheable(
            value = CacheConstants.INITIAL_AGENT,key = "T(com.console.framework.utils.TenantUtils).getTenantId()",
            firstCache = @FirstCache(expireTime = 5),secondaryCache = @SecondaryCache(expireTime = 1))
    public Integer getInitialAgentId() {
        QueryWrapper<Agent> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id").lambda().orderByAsc(Agent::getLevel).last("LIMIT 1");
        Agent agent = this.getOne(queryWrapper);
        return agent == null ? null : agent.getId();
    }

    /**
     * 通过代理等级ID获取代理等级详情
     * @param agentId   代理主键ID
     */
    @Override
    @Cacheable(value = CacheConstants.AGENT,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #agentId")
    public Agent getAgentById(Long agentId) {
        Agent agent = this.getById(agentId);
        QueryWrapper<Agent> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id").lambda().gt(Agent::getLevel,agent.getLevel()).orderByAsc(Agent::getLevel).last("limit 1");
        Agent nextLevel = this.getOne(queryWrapper);
        if (nextLevel != null) agent.setNextAgentId(nextLevel.getNextAgentId());
        return agent;
    }

    /**
     * 获取所有代理等级列表
     */
    @Override
    @Cacheable(value = CacheConstants.AGENT,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':LIST'")
    public List<Agent> getAgentList() {
        return this.list();
    }
}
