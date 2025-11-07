package com.jokardo.crm.order_service.domain.product;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Data
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "article", unique = true, nullable = false)
    private String productArticle;

    @Column(name = "name", nullable = false)
    private String productName;

    @Column(name = "price")
    private BigDecimal price;
}
