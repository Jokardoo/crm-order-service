package com.jokardo.crm.order_service.domain.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Product {
    private Long id;
    private String productArticle;
    private String productName;
    private BigDecimal price;
}
