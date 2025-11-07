package com.jokardo.crm.order_service.domain.address;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressDto {
    @NotBlank(message = "City should not be blank.")
    private String city;
    @NotBlank(message = "Street should not be blank.")
    private String street;

    private String postalCode;
}
