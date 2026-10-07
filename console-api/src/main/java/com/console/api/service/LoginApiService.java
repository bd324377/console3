package com.console.api.service;

import com.console.api.dto.req.LoginReq;
import com.console.framework.domain.LoginResult;

public interface LoginApiService {
    LoginResult login(LoginReq loginReq);
    String refreshUserToken(String refreshToken);
}
