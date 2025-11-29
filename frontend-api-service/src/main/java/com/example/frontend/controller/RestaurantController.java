package com.example.frontend.controller;

import com.example.frontend.dto.FilterRequest;
import com.example.frontend.dto.RestaurantCreateRequest;
import com.example.frontend.dto.RestaurantResponse;
import com.example.frontend.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService service;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MERCHANT','ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponse create(@Valid @RequestBody RestaurantCreateRequest req) {
        return service.create(req);
    }

    @GetMapping("/{id}")
    public RestaurantResponse get(@PathVariable String id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public RestaurantResponse update(@PathVariable String id, @Valid @RequestBody RestaurantCreateRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @PostMapping("/query")
    public Map<String, Object> query(@RequestBody FilterRequest req) {
        return service.query(req);
    }

}
