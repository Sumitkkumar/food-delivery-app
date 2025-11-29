package com.example.frontend.events;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record OrderCreatedEvent(
        String orderId,
        String userId,
        String username,
        String restaurantId,
        List<Map<String, Object>> items,
        Double subTotal,
        Double tax,
        Double total,
        Instant createdAt
) {}
