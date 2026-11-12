package com.console.api.service;

import com.console.api.dto.req.UserReq;
import com.console.api.dto.res.SubUserPageRes;

public interface AgentAPIService {
    SubUserPageRes getSubUserPage(UserReq.SearchSubUserReq searchSubUserReq);
}
