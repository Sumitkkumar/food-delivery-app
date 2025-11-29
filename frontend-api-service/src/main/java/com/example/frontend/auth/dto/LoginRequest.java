package com.example.frontend.auth.dto;

public record LoginRequest(
        String username,
        String password
) {}
