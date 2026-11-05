package com.console.api.service.impl;

import com.console.api.service.WalletAPIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WalletAPIServiceImpl extends BaseServiceImpl implements WalletAPIService {
    @Override
    public Object getWalletBalance() {
        return null;
    }
}
