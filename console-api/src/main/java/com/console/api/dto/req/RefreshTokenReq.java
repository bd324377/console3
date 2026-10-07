package com.console.api.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class RefreshTokenReq implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String refreshToken;
}
