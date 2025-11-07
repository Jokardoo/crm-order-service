package com.jokardo.crm.order_service.mapper.order;

import com.jokardo.crm.order_service.domain.order.Order;
import com.jokardo.crm.order_service.domain.order.OrderDto;
import com.jokardo.crm.order_service.mapper.ModelToDtoMapper;
import com.jokardo.crm.order_service.mapper.customer.CustomerModelToDtoMapper;
import com.jokardo.crm.order_service.mapper.orderItem.OrderItemModelToDtoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {CustomerModelToDtoMapper.class, OrderItemModelToDtoMapper.class})
public interface OrderModelToDtoMapper extends ModelToDtoMapper<Order, OrderDto> {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "items", source = "items"),
            @Mapping(target = "customer", source = "customer"),
            @Mapping(target = "deliveryAddress", ignore = true)
    })
    Order toModel(OrderDto dto);

    @Mappings({
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "items", source = "items"),
            @Mapping(target = "customer", source = "customer")
    })
    OrderDto toDto(Order model);
}
