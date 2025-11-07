package com.jokardo.crm.order_service.controller;

import com.jokardo.crm.order_service.domain.order.order_item_image.OrderItemImage;
import com.jokardo.crm.order_service.mapper.orderItem.OrderItemModelToEntityMapper;
import com.jokardo.crm.order_service.service.orderItem.OrderItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/order-item")
public class OrderItemController {

    private final OrderItemService orderItemService;
    private final OrderItemModelToEntityMapper orderItemModelToEntityMapper;

    @PostMapping("/{id}/upload-image")
    public ResponseEntity<HttpStatus> uploadOrderItemImage(@PathVariable(name = "id") Long orderItemId, @ModelAttribute OrderItemImage orderItemImage) {
        log.info("Called uploadOrderItemImage: {}", orderItemId);

        orderItemService.uploadOrderItemImage(orderItemId, orderItemImage);

        return ResponseEntity.status(HttpStatus.OK).body(HttpStatus.OK);
    }
}
