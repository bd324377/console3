package com.console.framework.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResult {
    private String token;
    private String refreshToken;
    private Integer userId;
}
