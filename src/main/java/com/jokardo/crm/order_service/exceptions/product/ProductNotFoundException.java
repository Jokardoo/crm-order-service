package com.jokardo.crm.order_service.exceptions.product;

public class ProductNotFoundException  extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
