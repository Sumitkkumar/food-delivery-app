package com.example.frontend.service;

import com.example.frontend.cache.RestaurantCacheService;
import com.example.frontend.common.utils.Helper;
import com.example.frontend.dto.FilterRequest;
import com.example.frontend.dto.RestaurantCreateRequest;
import com.example.frontend.dto.RestaurantResponse;
import com.example.frontend.exception.ForbiddenException;
import com.example.frontend.exception.NotFoundException;
import com.example.frontend.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository repo;
    private final RestaurantCacheService cache;

    public RestaurantResponse create(RestaurantCreateRequest req) {
        validate(req);
        var payload = Map.<String, Object>of(
                "name", req.name(),
                "cuisine", req.cuisine(),
                "city", req.city(),
                "rating", req.rating(),
                "createdBy", Helper.currentUser()
        );
        cache.evictAllRestaurantQueries();
        return map(repo.create(payload));
    }

    public Map<String, Object> query(FilterRequest req) {
        String cacheKey = DigestUtils.sha256Hex(req.toString());

        var cached = cache.getQueryResult(cacheKey);
        if (cached != null) return (Map<String, Object>) cached;
        // Build query payload
        Map<String, Object> payload = Map.of(
                "filters", req.getFilters(),
                "sort", req.getSort(),
                "page", req.getPage(),
                "size", req.getSize()
        );

        var response = repo.query(payload);

        cache.saveQueryResult(cacheKey, response);

        return response;
    }


    public RestaurantResponse get(String id) {
        // 1. Try from cache
        var cached = cache.getRestaurant(id);
        if (cached != null) return cached;

        // 2. Fetch from CRUD
        var result = repo.get(id);
        var mapped = map(result);

        // 3. Save to cache
        cache.saveRestaurant(id, mapped);

        return mapped;
    }

    public RestaurantResponse update(String id, RestaurantCreateRequest req) {
        validate(req);
        checkMerchantOwner(id);
        var payload = Map.<String, Object>of(
                "name", req.name(),
                "cuisine", req.cuisine(),
                "city", req.city(),
                "rating", req.rating()
        );
        cache.evictRestaurant(id);
        cache.evictAllRestaurantQueries();
        return map(repo.update(id, payload));
    }

    public void delete(String id) {
        checkMerchantOwner(id);
        cache.evictRestaurant(id);
        cache.evictAllRestaurantQueries();
        repo.delete(id);
    }

    private void validate(RestaurantCreateRequest req) {
        if (req.rating() != null && req.rating() > 4.8 && !"fine-dining".equalsIgnoreCase(req.cuisine())) {
            throw new IllegalArgumentException("Ratings above 4.8 allowed only for fine-dining cuisine.");
        }
    }

    private RestaurantResponse map(Map<String, Object> m) {
        return new RestaurantResponse(
                (String) m.get("id"),
                (String) m.get("name"),
                (String) m.get("cuisine"),
                (String) m.get("city"),
                m.get("rating") == null ? null : Double.valueOf(m.get("rating").toString()),
                (String) m.get("createdBy")
        );
    }

    private Instant parseInstant(Object o) {
        if (o == null) return null;
        if (o instanceof String s) return Instant.parse(s);
        if (o instanceof java.util.Date d) return d.toInstant();
        return null;
    }

    private void checkMerchantOwner(String id) {
        Map<String, Object> existing = repo.get(id);
        if (existing == null) {
            throw new NotFoundException("Restaurant not found");
        }

        String owner = (String) existing.get("createdBy");
        String current = Helper.currentUser();

        boolean isAdmin = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!current.equals(owner) && !isAdmin) {
            throw new ForbiddenException("You do not own this restaurant");
        }
    }
}
