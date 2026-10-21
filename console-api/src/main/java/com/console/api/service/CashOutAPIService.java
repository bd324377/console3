package com.console.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.console.api.dto.CashOutDTO;
import com.console.payment.entity.CashOutOrder;

public interface CashOutAPIService {
    void createCashOutOrder(CashOutDTO.CreateCashOutOrderReq cashOutOrderReq);
    IPage<CashOutOrder> getCashOutOrderRecords(CashOutDTO.SearchCashOutOrderReq searchCashOutOrderReq);
}
