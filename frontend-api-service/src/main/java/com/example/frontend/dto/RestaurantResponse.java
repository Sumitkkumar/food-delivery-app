package com.example.frontend.dto;

public record RestaurantResponse(
        String id,
        String name,
        String cuisine,
        String city,
        Double rating,
        String createdBy
) {}
