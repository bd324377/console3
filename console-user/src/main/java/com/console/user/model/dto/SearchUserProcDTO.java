package com.console.user.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchUserProcDTO {
    private Integer startSearchTime;//开始查询时间
    private Integer endSearchTime;//结束查询时间
    private Integer channelId;
    private List<Integer> channelIds;
}
