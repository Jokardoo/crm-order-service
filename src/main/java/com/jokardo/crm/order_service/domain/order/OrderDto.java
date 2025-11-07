package com.jokardo.crm.order_service.domain.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jokardo.crm.order_service.domain.customer.CustomerDto;
import com.jokardo.crm.order_service.domain.order.order_item.OrderItemDto;
import jakarta.validation.Valid;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String status;
    private List<@Valid OrderItemDto> items;
    private CustomerDto customer;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
}