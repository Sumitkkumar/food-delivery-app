package com.example.crud.controller;

import com.example.crud.dto.PageResponse;
import com.example.crud.dto.QueryRequest;
import com.example.crud.service.GenericCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/collections")
public class CollectionsController {

    private final GenericCrudService service;

    @PostMapping("{collection}")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@PathVariable String collection, @RequestBody Map<String, Object> body) {
        return service.create(collection, body);
    }

    @GetMapping("{collection}/{id}")
    public Map<String, Object> get(@PathVariable String collection, @PathVariable String id) {
        return service.get(collection, id);
    }

    @GetMapping("{collection}")
    public List<Map<String, Object>> list(@PathVariable String collection) {
        return service.list(collection);
    }

    @PostMapping("/{collection}/query")
    public PageResponse query(
            @PathVariable String collection,
            @RequestBody QueryRequest request
    ) {
        return service.query(collection, request);
    }


    @PutMapping("{collection}/{id}")
    public Map<String, Object> update(@PathVariable String collection, @PathVariable String id,
                                      @RequestBody Map<String, Object> body) {
        return service.update(collection, id, body);
    }

    @DeleteMapping("{collection}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String collection, @PathVariable String id) {
        service.delete(collection, id);
    }
}
