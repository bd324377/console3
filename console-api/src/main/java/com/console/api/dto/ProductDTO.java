package com.console.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

public class ProductDTO {
    @Getter
    @Setter
    public static class EnterProductRes implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @NotNull
        private Integer productId;    //产品ID
    }
}
