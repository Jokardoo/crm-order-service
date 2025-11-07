package com.jokardo.crm.order_service.domain.order.order_item;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jokardo.crm.order_service.domain.order.OrderDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class OrderItemDto {

    private String productArticle;

    @NotNull(message = "Item quantity should not be null!")
    @Positive(message = "Item quantity should be positive!")
    private int quantity;
    private String productName;

    private Integer indexInOrder;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> images;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private OrderDto order;
}

