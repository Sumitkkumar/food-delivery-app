package com.example.frontend.consumer;

import com.example.frontend.config.KafkaTopicConfig;
import com.example.frontend.events.OrderCreatedEvent;
import com.example.frontend.events.OrderStatusUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger("EVENT_CONSUMER_LOG");

    @KafkaListener(topics = KafkaTopicConfig.TOPIC_ORDER_CREATED)
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Received ORDER CREATED event: {}", event);
    }

    @KafkaListener(topics = KafkaTopicConfig.TOPIC_ORDER_STATUS)
    public void onOrderStatusUpdated(OrderStatusUpdatedEvent event) {
        log.info("Received ORDER STATUS UPDATED event: {}", event);
    }
}
