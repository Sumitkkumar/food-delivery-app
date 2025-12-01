package com.example.frontend.controller;

import com.example.frontend.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RestaurantController.class)
class RestaurantControllerTest {

    @Autowired MockMvc mvc;
    @MockBean RestaurantService service;
}
