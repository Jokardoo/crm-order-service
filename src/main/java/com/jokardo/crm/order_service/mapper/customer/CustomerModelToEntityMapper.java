package com.jokardo.crm.order_service.mapper.customer;

import com.jokardo.crm.order_service.domain.customer.Customer;
import com.jokardo.crm.order_service.domain.customer.CustomerEntity;
import com.jokardo.crm.order_service.mapper.ModelToEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerModelToEntityMapper extends ModelToEntityMapper<Customer, CustomerEntity> {

    @Mappings({
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "surname", source = "surname"),
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "phoneNumber", source = "phoneNumber")
    })
    Customer toModel(CustomerEntity entity);

    @Mappings({
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "surname", source = "surname"),
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "phoneNumber", source = "phoneNumber")
    })
    CustomerEntity toEntity(Customer model);

}
