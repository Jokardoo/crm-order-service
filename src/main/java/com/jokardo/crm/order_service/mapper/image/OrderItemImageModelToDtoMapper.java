package com.jokardo.crm.order_service.mapper.image;

import com.jokardo.crm.order_service.domain.order.order_item_image.OrderItemImage;
import com.jokardo.crm.order_service.domain.order.order_item_image.OrderItemImageDto;
import com.jokardo.crm.order_service.mapper.ModelToDtoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderItemImageModelToDtoMapper extends ModelToDtoMapper<OrderItemImage, OrderItemImageDto> {

    @Mappings({
            @Mapping(target = "file", source = "file")
    })
    OrderItemImage toModel(OrderItemImageDto dto);

    @Mappings({
            @Mapping(target = "file", source = "file")
    })
    OrderItemImageDto toDto(OrderItemImage dto);

    List<OrderItemImageDto> toDto(List<OrderItemImage> models);

    List<OrderItemImage> toModel(List<OrderItemImageDto> dtos);

}
