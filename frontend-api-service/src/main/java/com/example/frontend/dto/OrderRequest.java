package com.example.frontend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record OrderRequest(
        @NotBlank String userId,
        @NotBlank String restaurantId,
        @NotEmpty List<OrderItem> items,
        @NotNull @PositiveOrZero Double subTotal,
        @NotNull @PositiveOrZero Double tax,
        @NotNull @Positive Double total
) {}
