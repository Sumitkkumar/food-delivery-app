package com.example.crud.controller;

import com.example.crud.service.GenericCrudService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CollectionsController.class)
class CollectionsControllerTest {

    @Autowired MockMvc mvc;
    @MockBean GenericCrudService service;

    @Test
    void create_returnsCreated() throws Exception {
        when(service.create(eq("restaurants"), anyMap())).thenReturn(Map.of("id", "1", "name", "A"));
        mvc.perform(post("/collections/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"name":"A"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"));
    }
}
