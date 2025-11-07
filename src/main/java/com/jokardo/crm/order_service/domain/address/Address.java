package com.jokardo.crm.order_service.domain.address;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Address {
    private String city;
    private String street;
    private String postalCode;
}