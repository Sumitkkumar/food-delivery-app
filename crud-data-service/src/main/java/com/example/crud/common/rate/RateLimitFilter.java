package com.example.crud.common.rate;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import io.github.bucket4j.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(2) // runs AFTER RequestIdFilter
public class RateLimitFilter implements Filter {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    // DEFAULT LIMIT: 60 requests per minute per IP
    private Bucket newBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(7, Refill.greedy(7, Duration.ofMinutes(1))))
                .build();
    }

    // Stricter limit for expensive endpoints
    private Bucket newQueryBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1))))
                .build();
    }

    private Bucket resolveBucket(String key, boolean isQueryApi) {
        return cache.computeIfAbsent(key, k -> isQueryApi ? newQueryBucket() : newBucket());
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String ip = request.getRemoteAddr();
        String path = request.getRequestURI();

        boolean isQueryApi = path.contains("query"); // stricter limit

        Bucket bucket = resolveBucket(ip + path, isQueryApi);

        if (bucket.tryConsume(1)) {
            chain.doFilter(req, res);
        } else {
            response.setStatus(429);
            response.setContentType("application/json");

            response.getWriter().write("""
            {
              "status": 429,
              "message": "Too Many Requests — rate limit exceeded"
            }
            """);
        }
    }
}
