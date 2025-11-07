package com.jokardo.crm.order_service.domain.order.order_item;

import com.jokardo.crm.order_service.domain.order.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderItem {
    private Long id;

    private String productArticle;
    private int quantity;
    private String productName;

    private BigDecimal price;
    private List<String> images;
    private Integer indexInOrder;

    private Order order;
    private String description;

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", productArticle='" + productArticle + '\'' +
                ", quantity=" + quantity +
                ", productName='" + productName + '\'' +
                '}';
    }

    public OrderItem() {
        this.images = new ArrayList<>();
    }
}

