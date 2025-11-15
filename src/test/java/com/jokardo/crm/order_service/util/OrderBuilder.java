package com.jokardo.crm.order_service.util;

import com.jokardo.crm.order_service.domain.address.Address;
import com.jokardo.crm.order_service.domain.customer.Customer;
import com.jokardo.crm.order_service.domain.order.Order;
import com.jokardo.crm.order_service.domain.order.OrderStatusEnum;
import com.jokardo.crm.order_service.domain.order.order_item.OrderItem;
import lombok.Data;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Setter
public class OrderBuilder {

    private Long id;

    private OrderStatusEnum status;

    private LocalDateTime createdAt;

    private List<OrderItem> items;

    private Customer customer;

    private Address deliveryAddress;

    public static OrderBuilder builder() {
        return new OrderBuilder();
    }

    public OrderBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public OrderBuilder withStatus(OrderStatusEnum status) {
        this.status = status;
        return this;
    }

    public OrderBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public OrderBuilder withItems(List<OrderItem> items) {
        this.items = items;
        return this;
    }

    public OrderBuilder withCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public OrderBuilder withDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        return this;
    }

    public Order build() {
        Order order = new Order();

        order.setId(id);
        order.setStatus(status);
        order.setCreatedAt(createdAt);
        order.setItems(items);
        order.setCustomer(customer);
        order.setDeliveryAddress(deliveryAddress);

        return order;
    }
}
