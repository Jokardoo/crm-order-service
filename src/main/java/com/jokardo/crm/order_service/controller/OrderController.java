package com.jokardo.crm.order_service.controller;

import com.jokardo.crm.order_service.domain.order.Order;
import com.jokardo.crm.order_service.domain.order.OrderDto;
import com.jokardo.crm.order_service.domain.order.OrderRequest;
import com.jokardo.crm.order_service.domain.order.OrderUpdateRequest;
import com.jokardo.crm.order_service.mapper.order.OrderModelToDtoMapper;
import com.jokardo.crm.order_service.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/orders")
@AllArgsConstructor
@Tag(name = "Orders API", description = "Operations with orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderModelToDtoMapper orderModelToDtoMapper;
    private final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OrderDto> create(@ModelAttribute OrderRequest orderRequest) {
        logger.info("Creating order: {}", orderRequest);
        Order order = orderService.createOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderModelToDtoMapper.toDto(order));
    }


    @DeleteMapping("/{id}/delete")
    public ResponseEntity<HttpStatus> delete(@PathVariable Long id) {
        logger.info("Deleting order: {}", id);

        orderService.deleteByOrderId(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<OrderDto> updateOrderInfo(@PathVariable Long id, @Valid @RequestBody OrderUpdateRequest orderUpdateRequest) {
        logger.info("Called updateOrderInfo method: {}", orderUpdateRequest);

        return ResponseEntity.status(200)
                .body(orderModelToDtoMapper.toDto(orderService.updateOrderInfo(id, orderUpdateRequest)));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<HttpStatus> approveOrder(@PathVariable Long id) {
        logger.info("Called approveOrder method: {}", id);

        orderService.approveOrder(id);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping()
    @Operation(summary = "Get hello")
  //  @ApiResponses(value = @ApiResponse(responseCode = "200", description = "hello from OrdersController!"))
    public ResponseEntity<String> hello() {
        return ResponseEntity.status(HttpStatus.OK).body("hello");
    }



}
