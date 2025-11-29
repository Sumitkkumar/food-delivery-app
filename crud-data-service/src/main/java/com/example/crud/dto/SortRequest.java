package com.example.crud.dto;

import lombok.Data;

@Data
public class SortRequest {
    private String field;
    private String dir = "asc"; // asc/desc
}
