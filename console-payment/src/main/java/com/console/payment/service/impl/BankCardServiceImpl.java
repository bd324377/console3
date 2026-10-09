package com.console.payment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.console.payment.entity.BankCard;
import com.console.payment.mapper.*;
import com.console.payment.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankCardServiceImpl extends ServiceImpl<BankCardMapper,BankCard> implements BankCardService {

}
