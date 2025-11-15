package com.jokardo.crm.order_service.util;

import com.jokardo.crm.order_service.domain.address.AddressDto;
import com.jokardo.crm.order_service.domain.order.OrderRequest;
import com.jokardo.crm.order_service.domain.order.order_item.OrderItemRequest;
import com.jokardo.crm.order_service.util.order_item.OrderItemRequestBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderRequestBuilder {
    private String customerName = "John";
    private String customerSurname = "Doe";
    private String customerPhoneNumber = "+1234567890";
    private AddressDto deliveryAddress = createDefaultAddress();
    private List<OrderItemRequest> items = List.of(createDefaultOrderItem());

    public static OrderRequestBuilder builder() {
        return new OrderRequestBuilder();
    }

    public OrderRequestBuilder withCustomer(String name, String surname, String phone) {
        this.customerName = name;
        this.customerSurname = surname;
        this.customerPhoneNumber = phone;
        return this;
    }

    public OrderRequestBuilder withItem(String name, int quantity, String description) {
        OrderItemRequestBuilder orderItemRequestBuilder = new OrderItemRequestBuilder();
        OrderItemRequest orderItemRequest = orderItemRequestBuilder
                .withProductName(name)
                .withQuantity(quantity)
                .withDescription(description)
                .build();

        this.items = List.of(orderItemRequest);
        return this;
    }

    public OrderRequestBuilder withItems(List<OrderItemRequest> items) {
        this.items = items;
        return this;
    }

    public OrderRequest build() {
        OrderRequest request = new OrderRequest();
        request.setCustomerName(customerName);
        request.setCustomerSurname(customerSurname);
        request.setCustomerPhoneNumber(customerPhoneNumber);
        request.setDeliveryAddress(deliveryAddress);
        request.setItems(items);
        return request;
    }

    private static AddressDto createDefaultAddress() {
        AddressDto addressDto = new AddressDto();
        addressDto.setCity("New York");
        addressDto.setStreet("Long island");
        addressDto.setPostalCode("9410");
        return addressDto;
        // создание адреса по умолчанию
    }

    private static OrderItemRequest createDefaultOrderItem() {
        OrderItemRequest orderItemRequest = new OrderItemRequest();
        orderItemRequest.setName("Wardrobe");
        orderItemRequest.setQuantity(2);
        orderItemRequest.setDescription("Wardrobe 2000 x 800 x 400");

        return orderItemRequest;
        // создание элемента заказа по умолчанию
    }
}