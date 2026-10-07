package com.console.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.console.admin.dto.req.BackendUserReq;
import com.console.admin.entity.BackendUser;
import com.console.framework.domain.LoginResult;

import java.util.Map;

public interface BackendUserService extends IService<BackendUser> {
    boolean createAdminUser(BackendUserReq.CreateBackendUserReq createBackendUserReq);
    boolean createMarketer(BackendUserReq.CreateBackendUserReq createBackendUserReq);
    IPage<BackendUser> getAdminPage(BackendUserReq.SearchBackendUserReq searchBackendUserReq);
    Map<String,Object> getMarketerPage(BackendUserReq.SearchBackendUserReq searchBackendUserReq);
    LoginResult login(BackendUserReq.LoginReq loginReq);
    String refreshBackendUserToken(String refreshToken);
}
