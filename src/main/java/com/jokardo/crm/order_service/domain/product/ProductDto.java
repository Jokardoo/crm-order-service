package com.jokardo.crm.order_service.domain.product;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {

    @NotEmpty
    private String productArticle;

    @NotEmpty
    private String productName;

    @Positive
    @NotNull
    private BigDecimal price;
}
