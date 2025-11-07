package com.jokardo.crm.order_service.mapper.address;

import com.jokardo.crm.order_service.domain.address.Address;
import com.jokardo.crm.order_service.domain.address.AddressEntity;
import com.jokardo.crm.order_service.mapper.ModelToEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AddressModelToEntityMapper extends ModelToEntityMapper<Address, AddressEntity> {

    @Mappings({
            @Mapping(target = "city", source = "city"),
            @Mapping(target = "street", source = "street"),
            @Mapping(target = "postalCode", source = "postalCode")
    })
    Address toModel(AddressEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "city", source = "city"),
            @Mapping(target = "street", source = "street"),
            @Mapping(target = "postalCode", source = "postalCode")
    })
    AddressEntity toEntity(Address model);

}
