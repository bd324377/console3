package com.console.api.dto;

import com.console.framework.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

public class ProductOrderDTO {
    @Getter
    @Setter
    public static class SearchOrderReq {

        private Integer size;
        private Integer current;
        private Integer startSearchTime;    //起始下单时间
        private Integer endSearchTime;      //终止下单时间
        private Integer state;              //订单状态 1、有效 0、无效
        private Integer resultState;        //订单盈利状态：1、盈利；2、亏损；3、持平
        private Long    productId;          //产品id
        private Long    belongId;           //产品归属类型
        private Long    platformId;         //产商id
        private Long    userId;             //用户id
        private Integer channelId;          //分销渠道ID
        private List<Integer> channelIds;   //分销渠道ID列表
        private Double  resultMultiple;     //结果金额倍数
        private Integer amtSource = 0;      //默认查询真实战术
        private String  sortField = "order_time";//排序字段
        private Integer sortType = 1;       //排序类型 1、降序；2、升序

        public Integer getStartSearchTime() {
            return Objects.requireNonNullElse(startSearchTime, 0);
        }

        public Integer getEndSearchTime() {
            return Objects.requireNonNullElseGet(endSearchTime, DateUtils::getCurrentTimestamp);
        }
    }
}
