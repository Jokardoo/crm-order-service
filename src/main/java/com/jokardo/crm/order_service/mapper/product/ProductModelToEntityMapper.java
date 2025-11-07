package com.jokardo.crm.order_service.mapper.product;

import com.jokardo.crm.order_service.domain.product.Product;
import com.jokardo.crm.order_service.domain.product.ProductEntity;
import com.jokardo.crm.order_service.mapper.ModelToEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductModelToEntityMapper extends ModelToEntityMapper<Product, ProductEntity> {

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "productArticle", source = "productArticle"),
            @Mapping(target = "productName", source = "productName"),
            @Mapping(target = "price", source = "price")
    })
    Product toModel(ProductEntity entity);

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "productArticle", source = "productArticle"),
            @Mapping(target = "productName", source = "productName"),
            @Mapping(target = "price", source = "price")
    })
    ProductEntity toEntity(Product model);

    List<Product> toModel(List<ProductEntity> modelList);

    List<ProductEntity> toEntity(List<Product> modelList);

}
