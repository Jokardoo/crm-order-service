package com.jokardo.crm.order_service.mapper.orderItem;

import com.jokardo.crm.order_service.domain.order.OrderEntity;
import com.jokardo.crm.order_service.domain.order.order_item.OrderItem;
import com.jokardo.crm.order_service.domain.order.order_item.OrderItemEntity;
import com.jokardo.crm.order_service.mapper.ModelToEntityMapper;
import org.mapstruct.*;

import java.util.List;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderItemModelToEntityMapper extends ModelToEntityMapper<OrderItem, OrderItemEntity> {
    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "productArticle", source = "productArticle"),
            @Mapping(target = "quantity", source = "quantity"),
            @Mapping(target = "productName", source = "productName"),
            @Mapping(target = "indexInOrder", source = "indexInOrder"),
            @Mapping(target = "price", source = "price"),
            @Mapping(target = "description", source = "description"),
            @Mapping(target = "order", ignore = true)
    })
    OrderItem toModel(OrderItemEntity entity);

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "productArticle", source = "productArticle"),
            @Mapping(target = "quantity", source = "quantity"),
            @Mapping(target = "indexInOrder", source = "indexInOrder"),
            @Mapping(target = "productName", source = "productName"),
            @Mapping(target = "price", source = "price"),
            @Mapping(target = "description", source = "description"),
            @Mapping(target = "order", ignore = true)
    })
    OrderItemEntity toEntity(OrderItem model);



    List<OrderItemEntity> toEntity(List<OrderItem> orderItems);

    List<OrderItem> toModel(List<OrderItemEntity> orderItemEntities);

}
