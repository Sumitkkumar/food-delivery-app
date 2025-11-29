package com.example.frontend.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public record OrderRequest(
        @NotBlank String userId,
        @NotBlank String restaurantId,
        @NotEmpty List<OrderItem> items,
        @NotNull @PositiveOrZero Double subTotal,
        @NotNull @PositiveOrZero Double tax,
        @NotNull @Positive Double total
) {}
