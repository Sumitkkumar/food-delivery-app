package com.example.frontend.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    public static final String TOPIC_ORDER_CREATED = "order.created";
    public static final String TOPIC_ORDER_STATUS = "order.status.updated";

    @Bean
    public NewTopic orderCreatedTopic() {
        return new NewTopic(TOPIC_ORDER_CREATED, 3, (short) 1);
    }

    @Bean
    public NewTopic orderStatusTopic() {
        return new NewTopic(TOPIC_ORDER_STATUS, 3, (short) 1);
    }
}
