package com.console.api.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class ProductReq {
    @Getter
    @Setter
    public static class EnterProductReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @NotNull
        private Integer productId;    //产品ID
    }

    @Getter
    @Setter
    public static class CollectProductReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @NotNull
        private Integer productId;    //产品ID
    }

    @Getter
    @Setter
    public static class SearchProductOrderReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private long size = 20;          //每页条数
        private long current = 1;        //当前分页
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startSearchTime;    //开始查询时间
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endSearchTime;      //结束查询时间
        private Integer productId;    //产品ID
        private Integer category;    //产品分类
    }
}
