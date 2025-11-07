package com.jokardo.crm.order_service.exceptions.kafka;

public class KafkaFatalException extends RuntimeException {
    public KafkaFatalException(String message) {
        super(message);
    }
    public KafkaFatalException(String message, Throwable cause) { super(message, cause); }
}
