package com.jokardo.crm.order_service.domain.order;


import com.jokardo.crm.order_service.domain.address.Address;
import com.jokardo.crm.order_service.domain.customer.Customer;
import com.jokardo.crm.order_service.domain.order.order_item.OrderItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Order {
    private Long id;

    private OrderStatusEnum status;
    private LocalDateTime createdAt;

    private List<OrderItem> items;

    private Customer customer;

    private Address deliveryAddress;

    @Override
    public String toString() {
        return "Order{" +
                "status=" + status +
                ", createdAt=" + createdAt +
                ", items=" + items +
                ", customer=" + customer +
                '}';
    }

    public Order() {
        this.items = new ArrayList<>();
    }
}