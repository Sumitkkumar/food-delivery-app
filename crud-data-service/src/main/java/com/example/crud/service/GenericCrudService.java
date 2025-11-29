package com.example.crud.service;

import com.example.crud.dto.QueryRequest;
import com.example.crud.dto.PageResponse;
import com.example.crud.repository.GenericRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GenericCrudService {

    private final GenericRepository repo;

    public Map<String, Object> create(String collection, Map<String, Object> document) {
        return repo.create(collection, document);
    }

    public List<Map<String, Object>> list(String collection) { return repo.list(collection); }

    public Map<String, Object> get(String collection, String id) {
        return repo.get(collection, id);
    }

    public Map<String, Object> update(String collection, String id, Map<String, Object> payload) {
        return repo.update(collection, id, payload);
    }

    public void delete(String collection, String id) {
        repo.delete(collection, id);
    }

    public PageResponse query(String collection, QueryRequest request) {
        List<Map<String, Object>> items = repo.query(request, collection);
        long total = repo.count(request, collection);
        return PageResponse.of(items, total, request.getPage(), request.getSize());
    }
}
