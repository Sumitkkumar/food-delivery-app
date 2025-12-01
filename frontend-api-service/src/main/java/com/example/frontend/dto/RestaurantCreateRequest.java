package com.example.frontend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public record RestaurantCreateRequest(
        @NotBlank String name,
        @NotBlank String cuisine,
        @NotBlank String city,
        @DecimalMin("0.0") @DecimalMax("5.0") Double rating
) {}
