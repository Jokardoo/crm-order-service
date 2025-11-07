package com.jokardo.crm.order_service.domain.order.order_item;

import com.jokardo.crm.order_service.domain.order.OrderEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cascade;

import java.math.BigDecimal;
import java.util.List;

@Table(name = "order_item")
@Entity
@Data
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_article")
    private String productArticle;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "index_in_order")
    private Integer indexInOrder;

    @Column(name = "image")
    @CollectionTable(name = "orders_items_images")
    @ElementCollection  // т.к. это лист названия картинок
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> images;

    @ManyToOne()
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @Column(name = "description")
    private String description;
}

