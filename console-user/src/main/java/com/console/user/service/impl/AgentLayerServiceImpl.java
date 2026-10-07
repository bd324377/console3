package com.console.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.console.user.entity.AgentLayer;
import com.console.user.entity.User;
import com.console.user.mapper.AgentLayerMapper;
import com.console.user.model.bo.InviteNewConfig;
import com.console.user.service.AgentLayerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AgentLayerServiceImpl extends ServiceImpl<AgentLayerMapper, AgentLayer> implements AgentLayerService {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAgentLayerBatch(User user, InviteNewConfig inviteNewConfig) {
        if (user.getRealParentId() != null && user.getRealParentId() != 0) {
            List<AgentLayer> agentLayerList = new ArrayList<>();
            AgentLayer agentLayer = new AgentLayer();
            agentLayer.setUserId(user.getId());
            agentLayer.setSupUserId(user.getRealParentId());
            agentLayer.setLayerStatus(inviteNewConfig.getShuntStatus() ? 0 : 1);
            agentLayer.setLayers(1);
            agentLayerList.add(agentLayer);

            QueryWrapper<AgentLayer> agentLayerQueryWrapper = new QueryWrapper<>();
            agentLayerQueryWrapper.lambda().eq(AgentLayer::getUserId,user.getRealParentId());
            List<AgentLayer> supUserLayerList = this.list(agentLayerQueryWrapper);

            for (AgentLayer supLayer : supUserLayerList) {
                AgentLayer newAgentLayer = new AgentLayer();
                newAgentLayer.setUserId(user.getId());
                newAgentLayer.setSupUserId(supLayer.getSupUserId());
                newAgentLayer.setLayers(supLayer.getLayers() + 1);
                if (inviteNewConfig.getShuntStatus()) {//被分流用户标记分流状态
                    newAgentLayer.setLayerStatus(0);
                }
                agentLayerList.add(newAgentLayer);
            }
            this.saveBatch(agentLayerList);
        }
    }
}
