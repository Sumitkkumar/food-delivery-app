package com.example.frontend.repository;

import com.example.frontend.gateway.CrudGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class RestaurantRepository {

    private final CrudGateway gateway;

    @Value("${app.collections.restaurants}")
    private String collection;

    public Map<String, Object> create(Map<String, Object> payload) {
        return gateway.execute(gateway.client().create(collection, payload));
    }

    public Map<String, Object> get(String id) {
        return gateway.execute(gateway.client().getById(collection, id));
    }

    public Map<String, Object> update(String id, Map<String, Object> payload) {
        return gateway.execute(gateway.client().update(collection, id, payload));
    }

    public void delete(String id) {
        gateway.execute(gateway.client().delete(collection, id));
    }

    public Map<String, Object> query(Map<String, Object> payload) {
        return gateway.execute(gateway.client().query(collection, payload));
    }
}
