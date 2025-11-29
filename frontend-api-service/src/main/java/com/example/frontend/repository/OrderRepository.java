package com.example.frontend.repository;

import com.example.frontend.gateway.CrudGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final CrudGateway gateway;

    @Value("${app.collections.orders}")
    private String collection;

    public Map<String, Object> create(Map<String, Object> payload) {
        return gateway.execute(gateway.client().create(collection, payload));
    }

    public Map<String, Object> fetch(String id) {
        return gateway.execute(gateway.client().getById(collection, id));
    }

    public Map<String, Object> fetchMultiple(Map<String, Object> query) {
        return gateway.execute(gateway.client().query(collection, query));
    }

    public Map<String, Object> update(String id, Map<String, Object> payload) {
        return gateway.execute(gateway.client().update(collection, id, payload));
    }
}
