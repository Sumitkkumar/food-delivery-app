package com.example.crud.service;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenericCrudServiceTest {

    @Test
    void create_setsAuditFields() {
        MongoTemplate template = mock(MongoTemplate.class);
        GenericCrudService svc = new GenericCrudService(template);
        when(template.insert(any(Document.class), eq("restaurants")))
                .thenAnswer(inv -> inv.getArgument(0, Document.class));

        Map<String, Object> out = svc.create("restaurants", Map.of("name", "X"));

        assertEquals("X", out.get("name"));
        assertTrue(out.containsKey("_createdAt"));
        assertTrue(out.containsKey("_updatedAt"));
    }

    @Test
    void list_callsTemplate() {
        MongoTemplate template = mock(MongoTemplate.class);
        GenericCrudService svc = new GenericCrudService(template);
        svc.list("restaurants", null, 0, 10, null);
        verify(template, atLeastOnce()).find(any(Query.class), eq(Document.class), eq("restaurants"));
    }

    @Test
    void get_convertsId() {
        MongoTemplate template = mock(MongoTemplate.class);
        GenericCrudService svc = new GenericCrudService(template);
        var doc = new Document("_id", new ObjectId("6568d0f1b3b6a11f3a3e3a3e"));
        doc.put("name", "Y");
        when(template.findById(any(ObjectId.class), eq(Document.class), eq("restaurants"))).thenReturn(doc);

        var out = svc.getById("restaurants", "6568d0f1b3b6a11f3a3e3a3e");
        assertEquals("Y", out.get("name"));
        assertEquals("6568d0f1b3b6a11f3a3e3a3e", out.get("id"));
        assertFalse(out.containsKey("_id"));
    }
}
