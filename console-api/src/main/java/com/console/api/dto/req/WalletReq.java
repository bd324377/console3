package com.console.api.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class WalletReq {
    @Getter
    @Setter
    public static class SearchWalletLogReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private long    size = 20;          //每页条数
        private long    current = 1;        //当前分页
        private Integer userId;             //用户ID
        private Integer walletType = 1;     //钱包类型  0、用户钱包；1、测试钱包；2、代理钱包;3、公积金
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startSearchTime;    //开始查询时间
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endSearchTime;      //结束查询时间
        private List<Integer> procTypes;    //待查询过程类型列表
    }
}
