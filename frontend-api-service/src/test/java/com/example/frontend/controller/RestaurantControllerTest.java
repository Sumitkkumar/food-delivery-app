package com.example.frontend.controller;

import com.example.frontend.dto.RestaurantCreateRequest;
import com.example.frontend.dto.RestaurantResponse;
import com.example.frontend.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantController.class)
class RestaurantControllerTest {

    @Autowired MockMvc mvc;
    @MockBean RestaurantService service;

    @Test
    void list_ok() throws Exception {
        Mockito.when(service.list()).thenReturn(List.of(
                new RestaurantResponse("1", "A", "c1", "mumbai", 4.2, Instant.now(), Instant.now())
        ));
        mvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("A"));
    }

    @Test
    void create_ok() throws Exception {
        Mockito.when(service.create(any(RestaurantCreateRequest.class)))
                .thenReturn(new RestaurantResponse("1", "A", "c1", "mumbai", 4.2, Instant.now(), Instant.now()));
        mvc.perform(post("/api/restaurants").contentType(MediaType.APPLICATION_JSON)
                .content("{"name":"A","cuisine":"c1","city":"mumbai","rating":4.2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"));
    }
}
