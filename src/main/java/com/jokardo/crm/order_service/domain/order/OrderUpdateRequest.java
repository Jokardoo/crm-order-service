package com.jokardo.crm.order_service.domain.order;

import com.jokardo.crm.order_service.domain.address.AddressDto;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class OrderUpdateRequest {
    @Valid
    private AddressDto deliveryAddress;
}
