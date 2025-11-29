package com.example.frontend.dto;

import lombok.Data;
import java.util.List;

@Data
public class FilterRequest {
    private List<FilterItem> filters;
    private SortRequest sort;
    private Integer page = 0;
    private Integer size = 10;
}
