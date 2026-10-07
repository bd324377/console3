package com.console.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.user.entity.AgentLayer;
import com.console.user.entity.User;
import com.console.user.model.bo.InviteNewConfig;

public interface AgentLayerService extends IService<AgentLayer> {
    void addAgentLayerBatch(User user, InviteNewConfig inviteNewConfig);
}
