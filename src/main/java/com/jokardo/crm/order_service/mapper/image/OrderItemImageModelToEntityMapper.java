package com.jokardo.crm.order_service.mapper.image;

import com.jokardo.crm.order_service.domain.order.order_item_image.OrderItemImage;
import com.jokardo.crm.order_service.domain.order.order_item_image.OrderItemImageEntity;
import com.jokardo.crm.order_service.mapper.ModelToEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderItemImageModelToEntityMapper extends ModelToEntityMapper<OrderItemImage, OrderItemImageEntity> {

    @Mappings({
            @Mapping(target = "file", source = "file")
    })
    OrderItemImage toModel(OrderItemImageEntity entity);

    @Mappings({
            @Mapping(target = "file", source = "file")
    })
    OrderItemImageEntity toEntity(OrderItemImage model);

    List<OrderItemImageEntity> toEntity(List<OrderItemImage> models);

    List<OrderItemImage> toModel(List<OrderItemImageEntity> models);
}
