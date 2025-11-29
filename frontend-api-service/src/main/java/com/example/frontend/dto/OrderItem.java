package com.example.frontend.dto;

import lombok.Data;

@Data
public class OrderItem {
    private String name;
    private Integer qty;
    private Double price;
}

