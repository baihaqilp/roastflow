package com.flx.porto.roastflow.procurement.controller;

import com.flx.porto.roastflow.common.exception.DuplicateResourceException;
import com.flx.porto.roastflow.common.exception.GlobalExceptionHandler;
import com.flx.porto.roastflow.common.exception.ResourceNotFoundException;
import com.flx.porto.roastflow.procurement.model.dto.CherryLotResponse;
import com.flx.porto.roastflow.procurement.model.dto.CreateCherryLotRequest;
import com.flx.porto.roastflow.procurement.service.CherryLotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CherryLotController.class)
@Import(GlobalExceptionHandler.class)
class CherryLotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CherryLotService cherryLotService;

    @Test
    void shouldCreateCherryLot() throws Exception {

        UUID purchaseId = UUID.randomUUID();
        UUID lotId = UUID.randomUUID();

        CreateCherryLotRequest request =
                new CreateCherryLotRequest(
                        purchaseId,
                        "CH-2026-001",
                        new BigDecimal("20.000")
                );

        CherryLotResponse response =
                new CherryLotResponse(
                        lotId,
                        purchaseId,
                        "CH-2026-001",
                        new BigDecimal("20.000"),
                        new BigDecimal("20.000"),
                        "AVAILABLE",
                        OffsetDateTime.now(),
                        OffsetDateTime.now()
                );

        when(cherryLotService.create(any()))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/cherry-lots")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lotCode")
                        .value("CH-2026-001"))
                .andExpect(jsonPath("$.status")
                        .value("AVAILABLE"))
                .andExpect(jsonPath("$.initialWeightKg")
                        .value(20.000));
    }

    @Test
    void shouldRejectMissingPurchaseId() throws Exception {

        String request = """
                {
                    "lotCode": "CH-2026-001",
                    "initialWeightKg": 20.000
                }
                """;

        mockMvc.perform(
                        post("/api/v1/cherry-lots")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectBlankLotCode() throws Exception {

        String request = """
                {
                    "purchaseId": "%s",
                    "lotCode": "",
                    "initialWeightKg": 20.000
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(
                        post("/api/v1/cherry-lots")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectInvalidWeight() throws Exception {

        String request = """
                {
                    "purchaseId": "%s",
                    "lotCode": "CH-2026-001",
                    "initialWeightKg": 0
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(
                        post("/api/v1/cherry-lots")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundWhenPurchaseDoesNotExist() throws Exception {

        UUID purchaseId = UUID.randomUUID();

        CreateCherryLotRequest request =
                new CreateCherryLotRequest(
                        purchaseId,
                        "CH-2026-001",
                        new BigDecimal("20.000")
                );

        when(cherryLotService.create(any()))
                .thenThrow(new ResourceNotFoundException(
                        "Purchase not found with id: " + purchaseId
                ));

        mockMvc.perform(
                        post("/api/v1/cherry-lots")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnConflictForDuplicateLotCode() throws Exception {

        CreateCherryLotRequest request =
                new CreateCherryLotRequest(
                        UUID.randomUUID(),
                        "CH-2026-001",
                        new BigDecimal("20.000")
                );

        when(cherryLotService.create(any()))
                .thenThrow(new DuplicateResourceException(
                        "Cherry lot with code 'CH-2026-001' already exists"
                ));

        mockMvc.perform(
                        post("/api/v1/cherry-lots")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict());
    }

    @Test
    void shouldFindCherryLotById() throws Exception {

        UUID lotId = UUID.randomUUID();
        UUID purchaseId = UUID.randomUUID();

        CherryLotResponse response =
                new CherryLotResponse(
                        lotId,
                        purchaseId,
                        "CH-2026-001",
                        new BigDecimal("20.000"),
                        new BigDecimal("20.000"),
                        "AVAILABLE",
                        OffsetDateTime.now(),
                        OffsetDateTime.now()
                );

        when(cherryLotService.findById(lotId))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/cherry-lots/{id}", lotId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(lotId.toString()))
                .andExpect(jsonPath("$.lotCode")
                        .value("CH-2026-001"));
    }

    @Test
    void shouldReturnNotFoundWhenCherryLotDoesNotExist()
            throws Exception {

        UUID lotId = UUID.randomUUID();

        when(cherryLotService.findById(lotId))
                .thenThrow(new ResourceNotFoundException(
                        "Cherry lot not found with id: " + lotId
                ));

        mockMvc.perform(
                        get("/api/v1/cherry-lots/{id}", lotId)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFindAllCherryLots() throws Exception {

        CherryLotResponse response =
                new CherryLotResponse(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "CH-2026-001",
                        new BigDecimal("20.000"),
                        new BigDecimal("20.000"),
                        "AVAILABLE",
                        OffsetDateTime.now(),
                        OffsetDateTime.now()
                );

        when(cherryLotService.findAll())
                .thenReturn(List.of(response));

        mockMvc.perform(
                        get("/api/v1/cherry-lots")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].lotCode")
                        .value("CH-2026-001"));
    }
}