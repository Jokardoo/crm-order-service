package com.jokardo.crm.order_service.util;

import com.jokardo.crm.order_service.domain.customer.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerBuilder {

    private Long id;
    private String name;
    private String surname;
    private String phoneNumber;

    public static CustomerBuilder builder() {
        return new CustomerBuilder();
    }

    public CustomerBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CustomerBuilder withId(Long id) {
        this.id = id;
        return this;
    }


    public CustomerBuilder withSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public CustomerBuilder withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public Customer build() {
        Customer customer = new Customer();

        customer.setId(id);
        customer.setName(name);
        customer.setSurname(surname);
        customer.setPhoneNumber(phoneNumber);

        return customer;
    }

    public static Customer generateDefaultValidCustomer() {
        Customer customer = new Customer();

        customer.setName("John");
        customer.setSurname("Doe");
        customer.setPhoneNumber("88888888888");

        return customer;
    }


}
