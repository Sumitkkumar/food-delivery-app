package com.example.crud.dto;

import lombok.Data;

@Data
public class FilterItem {
    private String field;
    private String op;     // eq, ne, gt, gte, lt, lte, in, nin, contains
    private Object value;
}
