package com.jokardo.crm.order_service.domain.order.order_item;

import com.jokardo.crm.order_service.domain.order.order_item_image.OrderItemImage;
import com.jokardo.crm.order_service.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderItemUtil {
    private final ImageService imageService;

    public void updateAllOrderItemFields(OrderItemUpdateRequest updateRequest, OrderItem orderItemForUpdate) {
        if (updateRequest.getQuantity() != null)
            updateQuantity(updateRequest.getQuantity(), orderItemForUpdate);

        if (updateRequest.getOrderItemImage() != null && !updateRequest.getOrderItemImage().isEmpty())
            addNewImages(updateRequest.getOrderItemImage(), orderItemForUpdate);

    }

    public void updateQuantity(Integer quantity, OrderItem orderItem) {
        log.info("Called method updateQuantity");

        if (quantity == null || quantity < 0)
            throw new IllegalArgumentException("Quantity should not be null and should be greater than zero!");

        if (orderItem == null)
            throw new IllegalArgumentException("OrderItem should not be null!");

        orderItem.setQuantity(quantity);
    }

    public void addNewImages(List<OrderItemImage> orderItemImages, OrderItem orderItem) {
        log.info("Called method addNewImages");

        if (orderItemImages == null)
            throw new IllegalArgumentException("OrderItemImages should not be null!");

        if (orderItem == null)
            throw new IllegalArgumentException("OrderItem should not be null!");

        List<String> imageNames = new ArrayList<>();

        orderItemImages.forEach(orderItemImage -> {
            try {
                String curImageName = imageService.uploadOrderItemImage(orderItemImage);
                imageNames.add(curImageName);
            }
            catch (Exception e) {
                log.error(e.getMessage());
            }
        });

        orderItem.getImages().addAll(imageNames);
    }

}
