package com.console.core.service.impl;

import com.console.core.entity.Wallet;
import com.console.core.mapper.WalletMapper;
import com.console.core.service.WalletService;
import org.springframework.stereotype.Service;

@Service
public class WalletServiceImpl extends BaseServiceImpl<WalletMapper, Wallet> implements WalletService {
}
