package com.example.frontend.dto;

import java.util.List;

public record OrderResponse(
       String id,
       String userId,
       String restaurantId,
       List<OrderItem> items,
       Double subTotal,
       Double tax,
       Double total,
       OrderStatus status
) { }
