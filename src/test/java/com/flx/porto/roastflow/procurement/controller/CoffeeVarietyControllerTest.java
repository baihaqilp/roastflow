package com.flx.porto.roastflow.procurement.controller;

import com.flx.porto.roastflow.common.exception.DuplicateResourceException;
import com.flx.porto.roastflow.common.exception.GlobalExceptionHandler;
import com.flx.porto.roastflow.common.exception.ResourceNotFoundException;
import com.flx.porto.roastflow.procurement.model.dto.CoffeeVarietyResponse;
import com.flx.porto.roastflow.procurement.model.dto.CreateCoffeeVarietyRequest;
import com.flx.porto.roastflow.procurement.service.CoffeeVarietyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CoffeeVarietyController.class)
@Import(GlobalExceptionHandler.class)
class CoffeeVarietyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CoffeeVarietyService coffeeVarietyService;

    @Test
    void shouldCreateCoffeeVarietySuccessfully() throws Exception {

        CreateCoffeeVarietyRequest request =
                new CreateCoffeeVarietyRequest("Arabica");

        UUID id = UUID.randomUUID();

        CoffeeVarietyResponse response =
                new CoffeeVarietyResponse(
                        id,
                        "Arabica",
                        OffsetDateTime.now(),
                        OffsetDateTime.now()
                );

        when(coffeeVarietyService.create(any(CreateCoffeeVarietyRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/coffee-varieties")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Arabica"));
    }

    @Test
    void shouldReturnBadRequestWhenNameIsBlank() throws Exception {

        CreateCoffeeVarietyRequest request =
                new CreateCoffeeVarietyRequest("");

        mockMvc.perform(
                        post("/api/v1/coffee-varieties")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("name: Coffee variety name is required"));
    }

    @Test
    void shouldReturnConflictWhenCoffeeVarietyAlreadyExists()
            throws Exception {

        CreateCoffeeVarietyRequest request =
                new CreateCoffeeVarietyRequest("Arabica");

        when(coffeeVarietyService.create(any(CreateCoffeeVarietyRequest.class)))
                .thenThrow(
                        new DuplicateResourceException(
                                "Coffee variety already exists: Arabica"
                        )
                );

        mockMvc.perform(
                        post("/api/v1/coffee-varieties")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("Coffee variety already exists: Arabica"));
    }

    @Test
    void shouldFindCoffeeVarietyById() throws Exception {

        UUID id = UUID.randomUUID();

        CoffeeVarietyResponse response =
                new CoffeeVarietyResponse(
                        id,
                        "Arabica",
                        OffsetDateTime.now(),
                        OffsetDateTime.now()
                );

        when(coffeeVarietyService.findById(id))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/coffee-varieties/{id}", id)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Arabica"));
    }

    @Test
    void shouldReturnNotFoundWhenCoffeeVarietyDoesNotExist()
            throws Exception {

        UUID id = UUID.randomUUID();

        when(coffeeVarietyService.findById(id))
                .thenThrow(
                        new ResourceNotFoundException(
                                "Coffee variety not found: " + id
                        )
                );

        mockMvc.perform(
                        get("/api/v1/coffee-varieties/{id}", id)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Coffee variety not found: " + id));
    }

    @Test
    void shouldFindAllCoffeeVarieties() throws Exception {

        UUID arabicaId = UUID.randomUUID();
        UUID robustaId = UUID.randomUUID();

        CoffeeVarietyResponse arabica =
                new CoffeeVarietyResponse(
                        arabicaId,
                        "Arabica",
                        OffsetDateTime.now(),
                        OffsetDateTime.now()
                );

        CoffeeVarietyResponse robusta =
                new CoffeeVarietyResponse(
                        robustaId,
                        "Robusta",
                        OffsetDateTime.now(),
                        OffsetDateTime.now()
                );

        when(coffeeVarietyService.findAll())
                .thenReturn(List.of(arabica, robusta));

        mockMvc.perform(
                        get("/api/v1/coffee-varieties")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Arabica"))
                .andExpect(jsonPath("$[1].name").value("Robusta"));
    }
}