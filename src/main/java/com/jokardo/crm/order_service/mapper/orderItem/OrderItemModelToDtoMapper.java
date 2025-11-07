package com.jokardo.crm.order_service.mapper.orderItem;

import com.jokardo.crm.order_service.domain.order.order_item.OrderItem;
import com.jokardo.crm.order_service.domain.order.order_item.OrderItemDto;
import com.jokardo.crm.order_service.mapper.ModelToDtoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderItemModelToDtoMapper extends ModelToDtoMapper<OrderItem, OrderItemDto> {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "productArticle", source = "productArticle"),
            @Mapping(target = "quantity", source = "quantity"),
            @Mapping(target = "productName", source = "productName"),
            @Mapping(target = "indexInOrder", source = "indexInOrder"),
            @Mapping(target = "order", ignore = true), // Чтобы избежать циклической зависимости
            @Mapping(target = "price", ignore = true)
    })
    OrderItem toModel(OrderItemDto dto);

    @Mappings({
            @Mapping(target = "productArticle", source = "productArticle"),
            @Mapping(target = "quantity", source = "quantity"),
            @Mapping(target = "indexInOrder", source = "indexInOrder"),
            @Mapping(target = "productName", source = "productName"),
            @Mapping(target = "order", ignore = true)
    })
    OrderItemDto toDto(OrderItem model);
}
