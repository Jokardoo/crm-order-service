package com.jokardo.crm.order_service.exceptions.order;

public class OrderCannotBeUpdatedException extends RuntimeException {
    public OrderCannotBeUpdatedException(String message) {
        super(message);
    }
}
