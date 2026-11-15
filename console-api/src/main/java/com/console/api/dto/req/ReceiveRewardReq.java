package com.console.api.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiveRewardReq {
    @NotNull
    private Integer activityId;
    private Integer activityRuleId;
}
