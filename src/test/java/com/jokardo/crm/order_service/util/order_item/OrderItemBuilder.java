package com.jokardo.crm.order_service.util.order_item;

import com.jokardo.crm.order_service.domain.order.Order;
import com.jokardo.crm.order_service.domain.order.order_item.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderItemBuilder {

    private Long id;

    private String productArticle;
    private int quantity;
    private String productName;

    private BigDecimal price;
    private List<String> images;
    private Integer indexInOrder;

    private Order order;
    private String description;

    public static OrderItemBuilder builder() {
        return new OrderItemBuilder();
    }

    public OrderItemBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public OrderItemBuilder withProductArticle(String productArticle) {
        this.productArticle = productArticle;
        return this;
    }

    public OrderItemBuilder withQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public OrderItemBuilder withProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public OrderItemBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public OrderItemBuilder withImages(List<String> images) {
        this.images = images;
        return this;
    }

    public OrderItemBuilder withIndexInOrder(Integer indexInOrder) {
        this.indexInOrder = indexInOrder;
        return this;
    }

    public OrderItemBuilder withOrder(Order order) {
        this.order = order;
        return this;
    }

    public OrderItemBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public OrderItem build() {
        OrderItem orderItem = new OrderItem();

        orderItem.setId(id);
        orderItem.setProductArticle(productArticle);
        orderItem.setQuantity(quantity);
        orderItem.setProductName(productName);
        orderItem.setPrice(price);
        orderItem.setImages(images);
        orderItem.setIndexInOrder(indexInOrder);
        orderItem.setOrder(order);
        orderItem.setDescription(description);

        return orderItem;
    }

    public OrderItem generateDefaultOrderItem() {
        OrderItem orderItem = new OrderItem();

        orderItem.setQuantity(2);
        orderItem.setProductName("Product name");
        orderItem.setDescription("Description");

        return orderItem;
    }


}
