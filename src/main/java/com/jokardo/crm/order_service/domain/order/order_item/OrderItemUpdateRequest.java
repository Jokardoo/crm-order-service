package com.jokardo.crm.order_service.domain.order.order_item;

import com.jokardo.crm.order_service.domain.order.order_item_image.OrderItemImage;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class OrderItemUpdateRequest {
    @NotNull(message = "Order id should not be null1")
    @Positive(message = "Order id should be positive!")
    private Long orderId;

    @NotNull(message = "Order item id should not be null1")
    @Positive(message = "Order item id should be positive!")
    private Integer orderItemIndex;

    private List<OrderItemImage> orderItemImage;

    private String description;

    private Integer quantity;
}
