package com.console.user.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.console.core.entity.UserProc;
import com.console.user.entity.User;
import com.console.user.model.bo.InviteNewConfig;
import com.console.user.model.dto.req.UserReq;

import java.util.List;

public interface UserService extends IService<User> {
    List<JSONObject> batchCreateUsers(UserReq.CreateUserReq createUserReq);
    boolean updateUser(String field, UserReq.UpdateUserReq updateUserReq);
    IPage<Object> getUserPage(Page<User> userPage, UserReq.SearchUserReq searchUserReq);
    InviteNewConfig getUserInviteNewConfig(Integer userId);
    void syncNewUserConfig(User user,InviteNewConfig inviteNewConfig);
    void updateUserByLogin(UserProc userProc);
}
