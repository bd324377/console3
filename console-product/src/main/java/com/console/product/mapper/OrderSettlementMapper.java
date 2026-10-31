package com.console.product.mapper;

import com.console.product.entity.ProductOrder;
import org.apache.ibatis.annotations.Param;

public interface OrderSettlementMapper {
    ProductOrder selectNewestOrder(@Param("settledOrderId") Long settledOrderId,@Param("limitSize") Integer limitSize);
    void settlementOrderedAmt(@Param("fromId") Long fromId,@Param("toId") Long toId);//结算订单金额
    void insertOrderReport(@Param("fromId") Long fromId,@Param("toId") Long toId);
    void insertUserProcAboutOrder(@Param("fromId") Long fromId,@Param("toId") Long toId);
}
