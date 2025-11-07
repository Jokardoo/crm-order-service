package com.jokardo.crm.order_service.domain.order.order_item;

import com.jokardo.crm.order_service.domain.order.order_item_image.OrderItemImageDto;
import lombok.Data;

import java.util.List;

@Data
public class OrderItemRequest {
    private String productArticle;
    private int quantity;
    private String name;
    private String description;
    private List<OrderItemImageDto> images;

}
