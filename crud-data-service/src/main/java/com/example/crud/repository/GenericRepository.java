package com.example.crud.repository;

import com.example.crud.common.utils.Helper;
import com.example.crud.dto.FilterItem;
import com.example.crud.dto.QueryRequest;
import com.example.crud.common.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class GenericRepository {

    private final MongoTemplate mongoTemplate;

    public Map<String, Object> create(String collection, Map<String, Object> document) {
        Document doc = new Document(document);
        Instant now = Instant.now();
        doc.put("_createdAt", now);
        doc.put("_updatedAt", now);

        // insert will modify doc and set _id
        Document saved = mongoTemplate.insert(doc, collection);
        return Helper.normalizeDocument(saved);
    }

    public Map<String, Object> get(String collection, String id) {
        if (!ObjectId.isValid(id)) {
            throw new IllegalArgumentException("Invalid ID format: hexString must be 24 characters");
        }

        Map<String, Object> doc = mongoTemplate.findById(id, Map.class, collection);

        if (doc == null) {
            throw new NotFoundException("Document not found");
        }

        return Helper.normalizeDocument(doc);
    }

    public Map<String, Object> update(String collection, String id, Map<String, Object> payload) {
        if (!ObjectId.isValid(id)) {
            throw new IllegalArgumentException("Invalid ID format: hexString must be 24 characters");
        }

        Map<String, Object> existing = mongoTemplate.findById(id, Map.class, collection);
        if (existing == null) {
            throw new NotFoundException("Document not found");
        }

        existing.putAll(payload);
        existing.put("_updatedAt", Instant.now());
        mongoTemplate.save(existing, collection);

        return Helper.normalizeDocument(existing);
    }

    public void delete(String collection, String id) {
        if (!ObjectId.isValid(id)) {
            throw new IllegalArgumentException("Invalid ID format: hexString must be 24 characters");
        }

        Map<String, Object> existing = mongoTemplate.findById(id, Map.class, collection);
        if (existing == null) {
            throw new NotFoundException("Document not found");
        }

        mongoTemplate.remove(Query.query(Criteria.where("_id").is(id)), collection);
    }

    public List<Map<String, Object>> query(QueryRequest req, String collection) {
        Query query = buildQuery(req);
        query.skip((long) (req.getPage() - 1) * req.getSize());
        query.limit(req.getSize());

        if (req.getSort() != null && req.getSort().getField() != null) {
            query.with(Sort.by(
                    "desc".equalsIgnoreCase(req.getSort().getDir())
                            ? Sort.Direction.DESC
                            : Sort.Direction.ASC,
                    req.getSort().getField()
            ));
        }

        List<Map<String, Object>> items = (List<Map<String, Object>>) (List<?>) mongoTemplate.find(query, Map.class, collection);

        return items.stream()
                .map(Helper::normalizeDocument)
                .toList();
    }

    public List<Map<String, Object>> list(String collection) {
        return (List<Map<String, Object>>) (List<?>) mongoTemplate.findAll(Map.class, collection);
    }

    public long count(QueryRequest req, String collection) {
        return mongoTemplate.count(buildQuery(req), collection);
    }

    private Query buildQuery(QueryRequest req) {
        Criteria criteria = new Criteria();

        if (req.getFilters() != null && !req.getFilters().isEmpty()) {
            List<Criteria> criteriaList = new ArrayList<>();
            for (FilterItem f : req.getFilters()) {
                switch (f.getOp()) {
                    case "eq" -> criteriaList.add(Criteria.where(f.getField()).is(f.getValue()));
                    case "ne" -> criteriaList.add(Criteria.where(f.getField()).ne(f.getValue()));
                    case "gt" -> criteriaList.add(Criteria.where(f.getField()).gt(f.getValue()));
                    case "gte" -> criteriaList.add(Criteria.where(f.getField()).gte(f.getValue()));
                    case "lt" -> criteriaList.add(Criteria.where(f.getField()).lt(f.getValue()));
                    case "lte" -> criteriaList.add(Criteria.where(f.getField()).lte(f.getValue()));
                    case "contains" -> criteriaList.add(Criteria.where(f.getField())
                            .regex(f.getValue().toString(), "i"));
                }
            }
            criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        }

        return new Query(criteria);
    }
}
