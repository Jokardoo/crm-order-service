package com.jokardo.crm.order_service.mapper.product;

import com.jokardo.crm.order_service.domain.product.Product;
import com.jokardo.crm.order_service.domain.product.ProductDto;
import com.jokardo.crm.order_service.mapper.ModelToDtoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductModelToDtoMapper extends ModelToDtoMapper<Product, ProductDto> {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "productArticle", source = "productArticle"),
            @Mapping(target = "productName", source = "productName"),
            @Mapping(target = "price", source = "price")
    })
    Product toModel(ProductDto dto);

    @Mappings({
            @Mapping(target = "productArticle", source = "productArticle"),
            @Mapping(target = "productName", source = "productName"),
            @Mapping(target = "price", source = "price")
    })
    ProductDto toDto(Product model);

    List<ProductDto> toDto(List<Product> models);

    List<Product> toModel(List<ProductDto> dtos);


}
