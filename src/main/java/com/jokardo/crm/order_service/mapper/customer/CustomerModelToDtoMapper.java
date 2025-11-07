package com.jokardo.crm.order_service.mapper.customer;

import com.jokardo.crm.order_service.domain.customer.Customer;
import com.jokardo.crm.order_service.domain.customer.CustomerDto;
import com.jokardo.crm.order_service.mapper.ModelToDtoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerModelToDtoMapper extends ModelToDtoMapper<Customer, CustomerDto> {

    @Mappings({
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "surname", source = "surname"),
            @Mapping(target = "phoneNumber", source = "phoneNumber"),
            @Mapping(target = "id", ignore = true)
    })
    Customer toModel(CustomerDto dto);

    @Mappings({
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "surname", source = "surname"),
            @Mapping(target = "phoneNumber", source = "phoneNumber")
    })
    CustomerDto toDto(Customer model);

}
