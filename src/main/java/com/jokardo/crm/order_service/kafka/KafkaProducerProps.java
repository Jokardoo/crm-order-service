package com.jokardo.crm.order_service.kafka;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@AllArgsConstructor
public class KafkaProducerProps {

    @Value("${spring.kafka.producer.key-serializer}")
    private final String KEY_SERIALIZER;

    @Value("${spring.kafka.producer.value-serializer}")
    private final String VALUE_SERIALIZER;

    @Value("${spring.kafka.bootstrap-servers}")
    private final String BOOTSTRAP_SERVERS;

    @Value("${spring.kafka.producer.properties.acks}")
    private final String ACKS;

    @Value("${spring.kafka.producer.properties.retries}")
    private final int RETRIES;

    @Value("${spring.kafka.producer.properties.max.in.flight.requests.per.connection}")
    private final int MAX_IN_FLIGHT_REQUESTS;

    @Value("${spring.kafka.producer.properties.enable.idempotence}")
    private final boolean IDEMPOTENCE;

}
