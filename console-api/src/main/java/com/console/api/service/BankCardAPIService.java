package com.console.api.service;

import com.console.api.dto.req.BankCardReq;
import com.console.payment.entity.BankCard;

import java.util.List;

public interface BankCardAPIService {
    List<BankCard> getBankCardBankList();
    boolean addUserPhoneBankCard(BankCardReq.AddBankCardReq addBankCardReq);
    boolean addUserEmailBankCard(BankCardReq.AddBankCardReq addBankCardReq);
    boolean addUserBankCard(BankCardReq.AddBankCardReq addBankCardReq);
}
