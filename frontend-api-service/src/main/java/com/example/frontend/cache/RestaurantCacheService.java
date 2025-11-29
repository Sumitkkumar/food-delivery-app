package com.example.frontend.cache;

import com.example.frontend.dto.OrderResponse;
import com.example.frontend.dto.RestaurantResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RestaurantCacheService {

    private final RedisTemplate<String, Object> redis;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final String RESTAURANT_KEY_PREFIX = "restaurant::";
    private static final String QUERY_KEY_PREFIX = "restaurant_query::";
    private static final String ORDER_KEY_PREFIX = "order::";
    private static final String ORDER_QUERY_KEY_PREFIX = "order_query::";

    public RestaurantResponse getRestaurant(String id) {
        Object raw = redis.opsForValue().get(RESTAURANT_KEY_PREFIX + id);

        if (raw instanceof RestaurantResponse rr) {
            return rr;
        }
        if (raw instanceof Map m) {
            // Convert Map -> RestaurantResponse
            return mapper.convertValue(m, RestaurantResponse.class);
        }
        return null;
    }

    public OrderResponse getOrder(String id) {
        Object raw = redis.opsForValue().get(ORDER_KEY_PREFIX + id);

        if(raw instanceof  OrderResponse rr) {
            return rr;
        }
        if(raw instanceof Map m) {
            return mapper.convertValue(m, OrderResponse.class);
        }
        return null;
    }

    public void saveRestaurant(String id, RestaurantResponse response) {
        redis.opsForValue().set(
                RESTAURANT_KEY_PREFIX + id,
                response,
                Duration.ofMinutes(10)
        );
    }

    public void saveOrder(String id, OrderResponse response) {
        redis.opsForValue().set(
                ORDER_KEY_PREFIX + id,
                response,
                Duration.ofMinutes(30)
        );
    }

    public void evictRestaurant(String id) {
        redis.delete(RESTAURANT_KEY_PREFIX + id);
    }

    public void evictOrder(String id) {
        redis.delete(ORDER_KEY_PREFIX + id);
    }

    public void evictAllRestaurantQueries() {
        var keys = redis.keys(QUERY_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) redis.delete(keys);
    }

    public void evictAllMerchantOrderQueries() {
        var keys = redis.keys(ORDER_QUERY_KEY_PREFIX + "*");
        if(keys != null && !keys.isEmpty()) redis.delete(keys);
    }

    public Object getQueryResult(String key) {
        return redis.opsForValue().get(QUERY_KEY_PREFIX + key);
    }

    public Object getMerchantOrders(String key) {
        return redis.opsForValue().get(ORDER_QUERY_KEY_PREFIX + key);
    }

    public void saveQueryResult(String key, Object result) {
        redis.opsForValue().set(
                QUERY_KEY_PREFIX + key,
                result,
                Duration.ofSeconds(60)
        );
    }

    public void saveMerchantOrders(String key, Object result) {
        redis.opsForValue().set(
                ORDER_QUERY_KEY_PREFIX + key,
                result,
                Duration.ofSeconds(60)
        );
    }
}
