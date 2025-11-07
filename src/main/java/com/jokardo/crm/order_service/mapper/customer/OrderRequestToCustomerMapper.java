package com.jokardo.crm.order_service.mapper.customer;

import com.jokardo.crm.order_service.domain.customer.Customer;
import com.jokardo.crm.order_service.domain.order.OrderRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderRequestToCustomerMapper {

    @Mappings({
            @Mapping(target = "name", source = "customerName"),
            @Mapping(target = "surname", source = "customerSurname"),
            @Mapping(target = "phoneNumber", source = "customerPhoneNumber"),
            @Mapping(target = "id", ignore = true)
    })
    Customer toCustomer(OrderRequest orderRequest);

}
