package com.example.crud.controller;

import com.example.crud.service.GenericCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CollectionsController.class)
class CollectionsControllerTest {

    @Autowired MockMvc mvc;
    @MockBean GenericCrudService service;
}
