package com.example.crud.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor(staticName = "of")
public class PageResponse {
    private List<Map<String, Object>> items;
    private long total;
    private int page;
    private int size;
}
