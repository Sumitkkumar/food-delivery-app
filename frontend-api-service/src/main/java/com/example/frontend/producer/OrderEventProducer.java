package com.example.frontend.producer;

import com.example.frontend.config.KafkaTopicConfig;
import com.example.frontend.events.OrderCreatedEvent;
import com.example.frontend.events.OrderStatusUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger("EVENT_PRODUCER_LOG");

    public void publishOrderCreated(OrderCreatedEvent event) {
        kafkaTemplate.send(KafkaTopicConfig.TOPIC_ORDER_CREATED, event.orderId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Kafka Success: Sent OrderCreatedEvent to "
                                + KafkaTopicConfig.TOPIC_ORDER_CREATED + " | key=" + event.orderId());
                    } else {
                        log.info("Kafka ERROR sending OrderCreatedEvent: " + ex.getMessage());
                    }
                });
    }

    public void publishOrderStatusUpdated(OrderStatusUpdatedEvent event) {
        kafkaTemplate.send(KafkaTopicConfig.TOPIC_ORDER_STATUS, event.orderId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("Kafka Success: Sent OrderStatusUpdatedEvent to "
                                + KafkaTopicConfig.TOPIC_ORDER_STATUS + " | key=" + event.orderId());
                    } else {
                        System.err.println("Kafka ERROR sending OrderStatusUpdatedEvent: " + ex.getMessage());
                    }
                });
    }
}
