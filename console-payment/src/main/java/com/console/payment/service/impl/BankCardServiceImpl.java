package com.console.payment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.console.payment.entity.BankCard;
import com.console.payment.mapper.BankCardMapper;
import com.console.payment.service.BankCardService;
import org.springframework.stereotype.Service;

@Service
public class BankCardServiceImpl extends ServiceImpl<BankCardMapper, BankCard> implements BankCardService {
}
