package com.console.activity.dto.res;

import lombok.Getter;
import lombok.Setter;

public class ActivityRes {
    @Getter
    @Setter
    public static class ActivityBlurb {
        private Integer activityId;
        private String  acitivityName;
    }
}
