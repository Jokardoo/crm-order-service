package com.jokardo.crm.order_service.util.order_item;

import com.jokardo.crm.order_service.domain.order.order_item.OrderItemRequest;

import java.util.List;

public class OrderItemRequestBuilder {
    private Long id;

    private String productArticle;
    private int quantity;
    private String productName;

    private List<String> images;

    private String description;

    public OrderItemRequestBuilder withProductArticle(String productArticle) {
        this.productArticle = productArticle;
        return this;
    }

    public OrderItemRequestBuilder withQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public OrderItemRequestBuilder withProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public OrderItemRequestBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public OrderItemRequest build() {
        OrderItemRequest orderItemRequest = new OrderItemRequest();
        orderItemRequest.setName(productName);
        orderItemRequest.setDescription(description);
        orderItemRequest.setQuantity(quantity);
        orderItemRequest.setProductArticle(productArticle);

        return orderItemRequest;
    }

    public static OrderItemRequest buildDefaultValidOrderItemRequest() {
        OrderItemRequest orderItemRequest = new OrderItemRequest();
        orderItemRequest.setName("Table");
        orderItemRequest.setDescription("Table 1000 x 500 x 700");
        orderItemRequest.setQuantity(2);
        orderItemRequest.setProductArticle(null);

        return orderItemRequest;
    }


}
