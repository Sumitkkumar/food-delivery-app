package com.example.frontend.controller;

import com.example.frontend.dto.FilterRequest;
import com.example.frontend.dto.OrderResponse;
import com.example.frontend.dto.OrderRequest;
import com.example.frontend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderManagementController {

    private final OrderService service;

    @GetMapping
    public Map<String, Object> fetchOrders() { return service.getOrders(); };

    @PostMapping
    public OrderResponse createOrder(@Valid @RequestBody OrderRequest request) { return service.createOrder(request); }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable String id) { return service.getOrder(id); }

    @PostMapping("/restaurant")
    public Map<String, Object> getMerchantOrders(@RequestBody FilterRequest req) {
        return service.getMerchantOrders(req);
    }

    @PatchMapping("/{id}/{status}")
    public OrderResponse updateOrderStatus(@PathVariable String id, @PathVariable String status) { return service.updateStatus(id, status); }
}
