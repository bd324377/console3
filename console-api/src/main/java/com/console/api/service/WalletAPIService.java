package com.console.api.service;

import com.console.api.dto.req.WalletReq;

public interface WalletAPIService {
    Object getWalletBalance();
    Object getAgentWalletBalance();
    boolean setCashOutPwd(String password);
    boolean verifyCashOutPwd(String password);
    Object getWalletLog(WalletReq.SearchWalletLogReq searchWalletLogReq);
}
