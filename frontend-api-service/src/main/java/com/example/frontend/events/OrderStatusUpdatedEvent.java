package com.example.frontend.events;

import java.time.Instant;

public record OrderStatusUpdatedEvent(
        String orderId,
        String previousStatus,
        String newStatus,
        String updatedByUserId,
        String updatedByUsername,
        Instant updatedAt
) {}
