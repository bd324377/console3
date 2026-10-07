package com.console.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.core.entity.UserProc;
import com.console.user.entity.User;
import com.console.user.model.bo.InviteNewConfig;

public interface UserService extends IService<User> {
    InviteNewConfig getUserInviteNewConfig(Integer userId);
    void syncNewUserConfig(User user,InviteNewConfig inviteNewConfig);
    void updateUserByLogin(UserProc userProc);
}
