package com.jokardo.crm.order_service.exceptions.security;

public class NotEnoughRightsException extends RuntimeException {
    public NotEnoughRightsException(String message) {
        super(message);
    }
}
