package com.console.activity.task.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InviteNewSettlementParam {
    private Integer marketSiteId;//站点ID
    private Integer parentId;//代理用户ID
    private Integer subValidUserCount;//下级有效用户数量
}
