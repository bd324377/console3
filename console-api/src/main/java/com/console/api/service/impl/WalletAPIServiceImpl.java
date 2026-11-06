package com.console.api.service.impl;

import com.console.api.service.WalletAPIService;
import com.console.framework.request.RequestMember;
import com.console.framework.utils.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WalletAPIServiceImpl extends BaseServiceImpl implements WalletAPIService {
    @Override
    public Object getWalletBalance() {
        RequestMember requestMember = RequestUtils.getRequestMember();
        return null;
    }

    public Object setCashOutPwd() {
        return null;
    }


}
