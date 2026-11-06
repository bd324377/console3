package com.console.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.console.api.dto.CashOutDTO;
import com.console.api.dto.res.CashOutOrderPageRes;
import com.console.payment.entity.CashOutOrder;

public interface CashOutAPIService {
    void createCashOutOrder(CashOutDTO.CreateCashOutOrderReq cashOutOrderReq);
    CashOutOrderPageRes getCashOutOrderRecords(CashOutDTO.SearchCashOutOrderReq searchCashOutOrderReq);
}
