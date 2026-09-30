package com.flx.porto.roastflow.procurement.controller;

import com.flx.porto.roastflow.common.exception.DuplicateResourceException;
import com.flx.porto.roastflow.common.exception.GlobalExceptionHandler;
import com.flx.porto.roastflow.common.exception.ResourceNotFoundException;
import com.flx.porto.roastflow.procurement.model.dto.CreateFarmRequest;
import com.flx.porto.roastflow.procurement.model.dto.FarmResponse;
import com.flx.porto.roastflow.procurement.service.FarmService;

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
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FarmController.class)
@Import(GlobalExceptionHandler.class)
class FarmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FarmService farmService;


    @Test
    void shouldCreateFarmSuccessfully() throws Exception {

        CreateFarmRequest request = new CreateFarmRequest(
                "Kebun Pak Budi",
                "Banjarnegara, Jawa Tengah"
        );

        UUID farmId = UUID.randomUUID();

        FarmResponse response = new FarmResponse(
                farmId,
                "Kebun Pak Budi",
                "Banjarnegara, Jawa Tengah",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        when(farmService.create(any(CreateFarmRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/farms")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id")
                        .value(farmId.toString()))
                .andExpect(jsonPath("$.name")
                        .value("Kebun Pak Budi"))
                .andExpect(jsonPath("$.origin")
                        .value("Banjarnegara, Jawa Tengah"));
    }


    @Test
    void shouldReturnBadRequestWhenFarmNameIsBlank()
            throws Exception {

        CreateFarmRequest request = new CreateFarmRequest(
                "",
                "Banjarnegara, Jawa Tengah"
        );

        mockMvc.perform(
                        post("/api/v1/farms")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("name: Farm name is required"))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/farms"));
    }


    @Test
    void shouldReturnBadRequestWhenOriginIsBlank()
            throws Exception {

        CreateFarmRequest request = new CreateFarmRequest(
                "Kebun Pak Budi",
                ""
        );

        mockMvc.perform(
                        post("/api/v1/farms")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("origin: Origin is required"))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/farms"));
    }


    @Test
    void shouldReturnConflictWhenFarmAlreadyExists()
            throws Exception {

        CreateFarmRequest request = new CreateFarmRequest(
                "Kebun Pak Budi",
                "Banjarnegara, Jawa Tengah"
        );

        when(farmService.create(any(CreateFarmRequest.class)))
                .thenThrow(
                        new DuplicateResourceException(
                                "Farm already exists: Kebun Pak Budi"
                        )
                );

        mockMvc.perform(
                        post("/api/v1/farms")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status")
                        .value(409))
                .andExpect(jsonPath("$.error")
                        .value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("Farm already exists: Kebun Pak Budi"))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/farms"));
    }


    @Test
    void shouldFindFarmById() throws Exception {

        UUID farmId = UUID.randomUUID();

        FarmResponse response = new FarmResponse(
                farmId,
                "Kebun Pak Budi",
                "Banjarnegara, Jawa Tengah",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        when(farmService.findById(farmId))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/farms/{id}", farmId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(farmId.toString()))
                .andExpect(jsonPath("$.name")
                        .value("Kebun Pak Budi"))
                .andExpect(jsonPath("$.origin")
                        .value("Banjarnegara, Jawa Tengah"));
    }


    @Test
    void shouldReturnNotFoundWhenFarmDoesNotExist()
            throws Exception {

        UUID farmId = UUID.randomUUID();

        when(farmService.findById(farmId))
                .thenThrow(
                        new ResourceNotFoundException(
                                "Farm not found: " + farmId
                        )
                );

        mockMvc.perform(
                        get("/api/v1/farms/{id}", farmId)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.error")
                        .value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Farm not found: " + farmId))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/farms/" + farmId));
    }


    @Test
    void shouldFindAllFarms() throws Exception {

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FarmResponse farm1 = new FarmResponse(
                id1,
                "Kebun Pak Budi",
                "Banjarnegara, Jawa Tengah",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        FarmResponse farm2 = new FarmResponse(
                id2,
                "Kebun Pak Joko",
                "Wonosobo, Jawa Tengah",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        when(farmService.findAll())
                .thenReturn(List.of(farm1, farm2));

        mockMvc.perform(
                        get("/api/v1/farms")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()")
                        .value(2))
                .andExpect(jsonPath("$[0].name")
                        .value("Kebun Pak Budi"))
                .andExpect(jsonPath("$[0].origin")
                        .value("Banjarnegara, Jawa Tengah"))
                .andExpect(jsonPath("$[1].name")
                        .value("Kebun Pak Joko"))
                .andExpect(jsonPath("$[1].origin")
                        .value("Wonosobo, Jawa Tengah"));
    }
}