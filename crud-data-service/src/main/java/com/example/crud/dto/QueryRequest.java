package com.example.crud.dto;

import lombok.Data;
import java.util.List;

@Data
public class QueryRequest {
    private List<FilterItem> filters;
    private SortRequest sort;
    private Integer page = 0;
    private Integer size = 10;
}
