package com.jokardo.crm.order_service.domain.order;

import com.jokardo.crm.order_service.domain.address.AddressDto;
import com.jokardo.crm.order_service.domain.order.order_item.OrderItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    @NotEmpty(message = "items list should not be empty.")
    private List<@Valid OrderItemRequest> items;

    @NotBlank(message = "Customer phone number should not be empty!")
    @NotNull(message = "Customer phone number should not be null!")
    private String customerPhoneNumber;

    @NotBlank(message = "Customer name should not be empty!")
    @NotNull(message = "Customer name should not be null!")
    @Size(min = 2, max = 50, message = "Customer name size should be between 2 and 50 symbols!")
    private String customerName;

    @NotBlank(message = "Customer second name should not be empty!")
    @NotNull(message = "Customer second name should not be null!")
    @Size(min = 2, max = 50, message = "Customer second name size should be between 2 and 50 symbols!")
    private String customerSurname;

    @Valid
    private AddressDto deliveryAddress;
}