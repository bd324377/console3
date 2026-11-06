package com.console.api.service;

import com.console.api.dto.req.UserReq;
import com.console.api.dto.res.SelfInfoRes;
import com.console.user.entity.Avatar;

import java.util.List;

public interface UserAPIService {
    SelfInfoRes selfInfo();
    boolean bindPhone(UserReq.BindPhoneReq bindPhoneReq);
    boolean bindEmail(UserReq.BindEmailReq bindEmailReq);
    boolean updateLoginPwd(UserReq.UpdatePwdReq updatePwdReq);
    boolean updateSelfInfo(UserReq.UpdateUserInfoReq updateUserInfoReq);//更新自己的信息
    List<Avatar> getAvatarList(Integer sex);
}
