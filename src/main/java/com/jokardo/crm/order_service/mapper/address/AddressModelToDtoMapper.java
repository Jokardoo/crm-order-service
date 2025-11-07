package com.jokardo.crm.order_service.mapper.address;

import com.jokardo.crm.order_service.domain.address.Address;
import com.jokardo.crm.order_service.domain.address.AddressDto;
import com.jokardo.crm.order_service.mapper.ModelToDtoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AddressModelToDtoMapper extends ModelToDtoMapper<Address, AddressDto> {
    @Mappings({
            @Mapping(target = "city", source = "city"),
            @Mapping(target = "street", source = "street"),
            @Mapping(target = "postalCode", source = "postalCode")
    })
    Address toModel(AddressDto dto);

    @Mappings({
            @Mapping(target = "city", source = "city"),
            @Mapping(target = "street", source = "street"),
            @Mapping(target = "postalCode", source = "postalCode")
    })
    AddressDto toDto(Address model);

}
