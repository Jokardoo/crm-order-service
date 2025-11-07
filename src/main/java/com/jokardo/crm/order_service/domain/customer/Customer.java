package com.jokardo.crm.order_service.domain.customer;

import lombok.Data;

@Data
public class Customer {
    private Long id;
    private String name;
    private String surname;
    private String phoneNumber;
}
