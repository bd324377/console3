package com.console.api.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

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
        private Long productId;    //产品ID
    }
}
