package com.jokardo.crm.order_service.exceptions.customer;

public class CustomerPhoneNumberAlreadyExistsException extends RuntimeException {
    public CustomerPhoneNumberAlreadyExistsException(String message) {
        super(message);
    }
}
