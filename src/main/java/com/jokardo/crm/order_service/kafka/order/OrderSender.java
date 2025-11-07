package com.jokardo.crm.order_service.kafka.order;

import com.jokardo.crm.order_service.domain.order.Order;
import com.jokardo.crm.order_service.exceptions.kafka.KafkaFatalException;
import com.jokardo.crm.order_service.exceptions.kafka.KafkaSendException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderSender {

    private final KafkaTemplate<Long, Order> kafkaTemplate;
    private final AtomicInteger producerResetCount = new AtomicInteger(0);

    public void send(Order order) {
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {

            attempt++;
            log.debug("Attempt {} to send message.", attempt);

            try {
                var result = kafkaTemplate.send("order-topic", 1L, order);

                result.thenAccept(sendResult -> {
                            log.info("Order with id '{}' sent successfully", order.getId());
                        }

                );
                return;
            }
            catch (KafkaException e) {
                if (e instanceof OutOfOrderSequenceException) {
                    log.error("Fatal error occurred while sending order notification", e);
                }

                if (attempt == maxRetries) {
                    throw new KafkaFatalException("Failed to send message after " + maxRetries +
                            " attempts due to sequence issues", e);
                }

                // Ждем перед повторной попыткой
                waitBeforeRetry(attempt);
                resetProducerIfNeeded();
            }
        }
    }

    private void waitBeforeRetry(int attempt) {
        try {
            long delay = Math.min(1000 * (long) Math.pow(2, attempt), 30000); // Экспоненциальная backoff
            log.debug("Waiting {} ms before retry", delay);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaSendException("Retry wait interrupted", e);
        }
    }

    /**
     * Переинициализация продюсера (крайняя мера)
     */
    @SneakyThrows
    private void resetProducerIfNeeded() {

        int resetCount = producerResetCount.incrementAndGet();

        if (resetCount <= 3) { // Ограничиваем количество переинициализаций
            log.warn("Resetting Kafka producer due to sequence issues. Reset count: {}", resetCount);

            // Закрываем текущий продюсер
            kafkaTemplate.getProducerFactory().reset();

            // Ждем перед использованием нового продюсера
            Thread.sleep(1000L * resetCount);
        } else {
            log.error("Too many producer resets ({}). Possible configuration issue.", resetCount);
        }
    }


}
