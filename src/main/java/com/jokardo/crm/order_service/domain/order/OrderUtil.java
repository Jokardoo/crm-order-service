package com.jokardo.crm.order_service.domain.order;

import com.jokardo.crm.order_service.domain.address.Address;
import com.jokardo.crm.order_service.mapper.address.AddressModelToDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderUtil {
    private final AddressModelToDtoMapper addressModelToDtoMapper;


    public void updateAllOrderFields(Order order, OrderUpdateRequest orderUpdateRequest) {
        log.info("Called updateAllOrderFields: {}", order);
        updateOrderDeliveryAddress(addressModelToDtoMapper.toModel(orderUpdateRequest.getDeliveryAddress()), order);
    }

    public void updateOrderDeliveryAddress(Address deliveryAddress, Order order) {
        log.info("Called updateOrderDeliveryAddress: {}", order);

        if (deliveryAddress == null)
            throw new NullPointerException("DeliveryAddress should not be null!");

        if (order == null)
            throw new NullPointerException("Order should not be null!");

        order.setDeliveryAddress(deliveryAddress);
    }

}
