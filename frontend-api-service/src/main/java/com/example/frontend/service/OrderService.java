package com.example.frontend.service;

import com.example.frontend.cache.RestaurantCacheService;
import com.example.frontend.dto.*;
import com.example.frontend.events.OrderCreatedEvent;
import com.example.frontend.events.OrderStatusUpdatedEvent;
import com.example.frontend.exception.BadRequestException;
import com.example.frontend.exception.ForbiddenException;
import com.example.frontend.exception.NotFoundException;
import com.example.frontend.producer.OrderEventProducer;
import com.example.frontend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repo;
    private final RestaurantService restaurantService;
    private final RestaurantCacheService cache;
    private final OrderEventProducer producer;

    public Map<String, Object> getOrders() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, Object> payload = Map.of(
                "filters", List.of(
                        Map.of("field", "userId", "op", "eq", "value", userId)
                ),
                "page", 1,
                "size", 100
        );
        return repo.fetchMultiple(payload);
    }

    public OrderResponse createOrder(OrderRequest req) {
        var payload = Map.<String, Object>of(
                "userId", req.userId(),
                "restaurantId", req.restaurantId(),
                "items", req.items(),
                "subTotal", req.subTotal(),
                "tax", req.tax(),
                "total", req.total(),
                "status", "PLACED"
        );
        cache.evictAllMerchantOrderQueries();
        Map<String, Object> order = repo.create(payload);
        OrderResponse resp = map(order);

        // publish OrderCreatedEvent
        // note: items as generic list of maps (convert if necessary)
        @SuppressWarnings("unchecked")
        var items = (java.util.List<java.util.Map<String, Object>>) order.getOrDefault("items", java.util.List.of());

        String username = null;
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) username = auth.getName();

        OrderCreatedEvent evt = new OrderCreatedEvent(
                resp.id(),
                resp.userId(),
                username,
                resp.restaurantId(),
                items,
                resp.subTotal(),
                resp.tax(),
                resp.total(),
                Instant.now()
        );
        producer.publishOrderCreated(evt);

        return resp;
    }

    public OrderResponse getOrder(String id) {
        var cached = cache.getOrder(id);
        if(cached != null) return cached;
        Map<String, Object> order = repo.fetch(id);
        if(order == null) {
            throw new NotFoundException("Order not found!");
        }
        var result = map(order);
        cache.saveOrder(id, result);
        return result;
    }

    public Map<String, Object> getMerchantOrders(FilterRequest req) {
        String cacheKey = DigestUtils.sha256Hex(req.toString());
        var cached = cache.getMerchantOrders(cacheKey);
        if(cached != null) return (Map<String, Object>) cached;
        // Build query payload
        Map<String, Object> payload = Map.of(
                "filters", req.getFilters(),
                "page", req.getPage(),
                "size", req.getSize()
        );

        var result = repo.fetchMultiple(payload);

        cache.saveMerchantOrders(cacheKey, result);

        return result;
    }

    public OrderResponse updateStatus(String id, String status) {
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(status);
        } catch (Exception e) {
            throw new BadRequestException("Invalid Status!");
        }
        OrderResponse existingOrder = getOrder(id);

        String username  = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = hasRole("ROLE_ADMIN");
        boolean isMerchant = hasRole("ROLE_MERCHANT");
        boolean isUser = !isAdmin && !isMerchant;

        //Other checks....
        if(isMerchant) {
             RestaurantResponse restaurant = restaurantService.get(existingOrder.restaurantId());
             if(!Objects.equals(restaurant.createdBy(), username)) {
                 throw new ForbiddenException("You are not the owner of this restaurant!");
             }
        }
        if(isUser) {
            if(!Objects.equals(username, existingOrder.userId())) {
                throw new ForbiddenException("You can only change your own orders!");
            }
        }
        if(!isValidTransition(existingOrder.status(), newStatus, isUser, isAdmin, isMerchant)) {
            throw new BadRequestException("Invalid status transition from \" + currentStatus + \" to \" + newStatus");
        }
        var payload = Map.<String, Object>of(
                "userId", existingOrder.userId(),
                "restaurantId", existingOrder.restaurantId(),
                "items", existingOrder.items(),
                "subTotal", existingOrder.subTotal(),
                "tax", existingOrder.tax(),
                "total", existingOrder.total(),
                "status", newStatus.name()
        );
        cache.evictOrder(id);
        cache.evictAllMerchantOrderQueries();

        Map<String, Object> updated = repo.update(id, payload);
        OrderResponse resp = map(updated);

        // publish status updated event
        String userId = null;
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            if (auth.getDetails() instanceof java.util.Map<?, ?> d && d.get("userId") != null) {
                userId = d.get("userId").toString();
            }
        }

        OrderStatusUpdatedEvent evt = new OrderStatusUpdatedEvent(
                resp.id(),
                existingOrder.status().name(),
                resp.status() == null ? status : resp.status().name(),
                userId,
                username,
                Instant.now()
        );
        producer.publishOrderStatusUpdated(evt);

        return resp;
    }

    private OrderResponse map(Map<String, Object> m) {
        if (m == null) return null;

        String id = safeString(m.get("id"));
        String userId = safeString(m.get("userId"));
        String restaurantId = safeString(m.get("restaurantId"));

        List<OrderItem> items = convertItems(m.get("items"));

        Double subTotal = toDouble(m.get("subTotal"));
        Double tax = toDouble(m.get("tax"));
        Double total = toDouble(m.get("total"));

        OrderStatus status = parseOrderStatus(m.get("status"));

        return new OrderResponse(id, userId, restaurantId, items, subTotal, tax, total, status);
    }

    // helper to parse OrderStatus from db representation
    private OrderStatus parseOrderStatus(Object raw) {
        if (raw == null) return null;
        if (raw instanceof OrderStatus) return (OrderStatus) raw;
        return OrderStatus.valueOf(raw.toString());
    }

    private Double toDouble(Object raw) {
        if (raw == null) return null;
        if (raw instanceof Number n) return n.doubleValue();
        try {
            return Double.parseDouble(raw.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private String safeString(Object o) {
        return o == null ? null : o.toString();
    }

    @SuppressWarnings("unchecked")
    private List<OrderItem> convertItems(Object raw) {
        if (raw == null) return List.of();
        if (raw instanceof List<?> list) {
            List<OrderItem> out = new ArrayList<>();
            for (Object o : list) {
                if (o instanceof OrderItem ri) {
                    out.add(ri);
                } else if (o instanceof Map<?, ?> map) {
                    Map<String, Object> mm = (Map<String, Object>) map;
                    String name = safeString(mm.get("name"));
                    int qty = mm.get("qty") == null ? 0 : ((Number) mm.get("qty")).intValue();
                    Double price = toDouble(mm.get("price"));
                    OrderItem item = new OrderItem();
                    item.setName(name);
                    item.setQty(qty);
                    item.setPrice(price);
                    out.add(item);
                }
            }
            return out;
        }
        return List.of();
    }

    private boolean hasRole(String role) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(role::equals);
    }

    private boolean isValidTransition(OrderStatus currentState, OrderStatus newState, boolean isUser, boolean isAdmin, boolean isMerchant) {
        if(currentState == newState) return true;

        if(isAdmin) return true;

        if(isUser) {
            if(newState == OrderStatus.CANCELLED) return currentState == OrderStatus.PLACED;
        }

        if(isMerchant) {
            if(newState == OrderStatus.REJECTED) return currentState == OrderStatus.PLACED;
        }

        // standard allowed forward transitions
        return switch (currentState) {
            case PLACED -> (newState == OrderStatus.ACCEPTED || newState == OrderStatus.REJECTED || newState == OrderStatus.CANCELLED);
            case ACCEPTED -> (newState == OrderStatus.PREPARING || newState == OrderStatus.CANCELLED);
            case PREPARING -> (newState == OrderStatus.READY);
            case READY -> (newState == OrderStatus.OUT_FOR_DELIVERY);
            case OUT_FOR_DELIVERY -> (newState == OrderStatus.DELIVERED);
            default -> false;
        };
    }
}
