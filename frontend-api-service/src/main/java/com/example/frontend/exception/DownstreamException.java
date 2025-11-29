package com.example.frontend.exception;

import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DownstreamException extends RuntimeException {

    private final int statusCode;
    private final Object parsedBody; // can be Map or String

    public DownstreamException(String rawBody, int statusCode) {
        super(rawBody);
        this.statusCode = statusCode;

        Object parsed;
        try {
            parsed = new ObjectMapper().readValue(rawBody, Map.class);
        } catch (Exception e) {
            parsed = Map.of("message", rawBody, "status", statusCode);
        }
        this.parsedBody = parsed;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Object getParsedBodyOrMessage() {
        return parsedBody;
    }
}
