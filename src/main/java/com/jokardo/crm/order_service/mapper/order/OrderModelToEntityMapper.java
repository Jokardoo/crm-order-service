package com.jokardo.crm.order_service.mapper.order;

import com.jokardo.crm.order_service.domain.order.Order;
import com.jokardo.crm.order_service.domain.order.OrderEntity;
import com.jokardo.crm.order_service.domain.order.order_item.OrderItem;
import com.jokardo.crm.order_service.domain.order.order_item.OrderItemEntity;
import com.jokardo.crm.order_service.mapper.customer.CustomerModelToEntityMapper;
import com.jokardo.crm.order_service.mapper.orderItem.OrderItemModelToEntityMapper;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {CustomerModelToEntityMapper.class, OrderItemModelToEntityMapper.class})
public interface OrderModelToEntityMapper {

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "items", ignore = true),
            @Mapping(target = "customer", source = "customer"),
            @Mapping(target = "deliveryAddress", source = "deliveryAddress")
    })
    Order toModel(OrderEntity entity);

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "items", ignore = true),
            @Mapping(target = "customer", source = "customer"),
            @Mapping(target = "deliveryAddress", source = "deliveryAddress")
    })
    OrderEntity toEntity(Order model);

    @AfterMapping
    default void setupOrderItemRelations(@MappingTarget OrderEntity orderEntity) {
        if (orderEntity.getItems() != null) {
            for (OrderItemEntity itemEntity : orderEntity.getItems()) {
                itemEntity.setOrder(orderEntity);
            }
        }
    }



}

