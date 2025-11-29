package com.example.crud.common.utils;

import java.util.HashMap;
import java.util.Map;

public class Helper {
    public static Map<String, Object> normalizeDocument(Map<String, Object> doc) {
        if (doc == null) return null;

        Map<String, Object> clean = new HashMap<>();

        // Copy all fields except internal ones
        for (var e : doc.entrySet()) {
            String key = e.getKey();

            if (key.equals("_id")) continue;
            if (key.equals("_updatedAt")) continue;
            if (key.equals("_createdAt")) continue;

            clean.put(key, e.getValue());
        }

        // add createdAt
        Object created = doc.get("_createdAt");
        if (created != null) {
            clean.put("createdAt", created);
        }

        // add id
        Object rawId = doc.get("_id");
        if (rawId instanceof org.bson.types.ObjectId oid) {
            clean.put("id", oid.toHexString());
        }

        return clean;
    }
}
