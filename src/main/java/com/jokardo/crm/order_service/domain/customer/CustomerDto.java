package com.jokardo.crm.order_service.domain.customer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerDto {
    @NotBlank(message = "Name should not be blank.")
    private String name;
    @NotBlank(message = "Surname should not be blank.")
    private String surname;
    @NotBlank(message = "Phone number should not be  blank.")
    private String phoneNumber;
}
