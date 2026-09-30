package com.flx.porto.roastflow.procurement.controller;


import com.flx.porto.roastflow.common.exception.DuplicateResourceException;
import com.flx.porto.roastflow.common.exception.GlobalExceptionHandler;
import com.flx.porto.roastflow.common.exception.ResourceNotFoundException;
import com.flx.porto.roastflow.procurement.model.dto.CreatePurchaseRequest;
import com.flx.porto.roastflow.procurement.model.dto.PurchaseResponse;
import com.flx.porto.roastflow.procurement.service.PurchaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PurchaseController.class)
@Import(GlobalExceptionHandler.class)
class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PurchaseService purchaseService;

    @Test
    void shouldCreatePurchaseSuccessfully() throws Exception {

        UUID supplierId = UUID.randomUUID();
        UUID farmId = UUID.randomUUID();
        UUID varietyId = UUID.randomUUID();
        UUID purchaseId = UUID.randomUUID();

        CreatePurchaseRequest request = new CreatePurchaseRequest(
                supplierId,
                farmId,
                varietyId,
                LocalDate.of(2026, 9, 30),
                new BigDecimal("35000"),
                new BigDecimal("10.500")
        );

        PurchaseResponse response = new PurchaseResponse(
                purchaseId,
                supplierId,
                "Pak Budi",
                farmId,
                "Kebun Pak Budi",
                "Banjarnegara, Jawa Tengah",
                varietyId,
                "Sigarar Utang",
                LocalDate.of(2026, 9, 30),
                new BigDecimal("35000"),
                new BigDecimal("10.500"),
                new BigDecimal("367500"),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        when(purchaseService.create(any(CreatePurchaseRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(purchaseId.toString()))
                .andExpect(jsonPath("$.supplierId").value(supplierId.toString()))
                .andExpect(jsonPath("$.supplierName").value("Pak Budi"))
                .andExpect(jsonPath("$.farmId").value(farmId.toString()))
                .andExpect(jsonPath("$.farmName").value("Kebun Pak Budi"))
                .andExpect(jsonPath("$.varietyId").value(varietyId.toString()))
                .andExpect(jsonPath("$.varietyName").value("Sigarar Utang"))
                .andExpect(jsonPath("$.purchaseDate").value("2026-09-30"))
                .andExpect(jsonPath("$.pricePerKg").value(35000))
                .andExpect(jsonPath("$.totalWeightKg").value(10.5))
                .andExpect(jsonPath("$.totalAmount").value(367500));

        verify(purchaseService).create(any(CreatePurchaseRequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenSupplierIdIsMissing() throws Exception {

        String request = """
                {
                    "farmId": "%s",
                    "varietyId": "%s",
                    "purchaseDate": "2026-09-30",
                    "pricePerKg": 35000,
                    "totalWeightKg": 10.500
                }
                """.formatted(
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        mockMvc.perform(post("/api/v1/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString("Supplier ID is required")));

        verifyNoInteractions(purchaseService);
    }

    @Test
    void shouldReturnBadRequestWhenPriceIsInvalid() throws Exception {

        String request = """
                {
                    "supplierId": "%s",
                    "farmId": "%s",
                    "varietyId": "%s",
                    "purchaseDate": "2026-09-30",
                    "pricePerKg": 0,
                    "totalWeightKg": 10.500
                }
                """.formatted(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        mockMvc.perform(post("/api/v1/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString(
                                "Price per kg must be greater than zero"
                        )));

        verifyNoInteractions(purchaseService);
    }

    @Test
    void shouldReturnBadRequestWhenWeightIsInvalid() throws Exception {

        String request = """
                {
                    "supplierId": "%s",
                    "farmId": "%s",
                    "varietyId": "%s",
                    "purchaseDate": "2026-09-30",
                    "pricePerKg": 35000,
                    "totalWeightKg": 0
                }
                """.formatted(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        mockMvc.perform(post("/api/v1/purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString(
                                "Total weight must be greater than zero"
                        )));

        verifyNoInteractions(purchaseService);
    }

    @Test
    void shouldReturnNotFoundWhenSupplierDoesNotExist() throws Exception {

        UUID supplierId = UUID.randomUUID();

        CreatePurchaseRequest request = new CreatePurchaseRequest(
                supplierId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.of(2026, 9, 30),
                new BigDecimal("35000"),
                new BigDecimal("10.500")
        );

        when(purchaseService.create(any(CreatePurchaseRequest.class)))
                .thenThrow(new ResourceNotFoundException(
                        "Supplier not found: " + supplierId
                ));

        mockMvc.perform(post("/api/v1/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Supplier not found: " + supplierId));
    }

    @Test
    void shouldFindPurchaseById() throws Exception {

        UUID purchaseId = UUID.randomUUID();
        UUID supplierId = UUID.randomUUID();
        UUID farmId = UUID.randomUUID();
        UUID varietyId = UUID.randomUUID();

        PurchaseResponse response = new PurchaseResponse(
                purchaseId,
                supplierId,
                "Pak Budi",
                farmId,
                "Kebun Pak Budi",
                "Banjarnegara, Jawa Tengah",
                varietyId,
                "Sigarar Utang",
                LocalDate.of(2026, 9, 30),
                new BigDecimal("35000"),
                new BigDecimal("10.500"),
                new BigDecimal("367500"),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        when(purchaseService.findById(purchaseId))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/purchases/{id}", purchaseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(purchaseId.toString()))
                .andExpect(jsonPath("$.supplierName").value("Pak Budi"))
                .andExpect(jsonPath("$.farmName").value("Kebun Pak Budi"))
                .andExpect(jsonPath("$.varietyName").value("Sigarar Utang"))
                .andExpect(jsonPath("$.totalAmount").value(367500));

        verify(purchaseService).findById(purchaseId);
    }

    @Test
    void shouldReturnNotFoundWhenPurchaseDoesNotExist() throws Exception {

        UUID purchaseId = UUID.randomUUID();

        when(purchaseService.findById(purchaseId))
                .thenThrow(new ResourceNotFoundException(
                        "Purchase not found: " + purchaseId
                ));

        mockMvc.perform(get("/api/v1/purchases/{id}", purchaseId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Purchase not found: " + purchaseId));
    }

    @Test
    void shouldFindAllPurchases() throws Exception {

        UUID purchaseId1 = UUID.randomUUID();
        UUID purchaseId2 = UUID.randomUUID();

        PurchaseResponse purchase1 = new PurchaseResponse(
                purchaseId1,
                UUID.randomUUID(),
                "Pak Budi",
                UUID.randomUUID(),
                "Kebun Budi",
                "Banjarnegara",
                UUID.randomUUID(),
                "Sigarar Utang",
                LocalDate.of(2026, 9, 29),
                new BigDecimal("35000"),
                new BigDecimal("10.000"),
                new BigDecimal("350000"),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        PurchaseResponse purchase2 = new PurchaseResponse(
                purchaseId2,
                UUID.randomUUID(),
                "Pak Joko",
                UUID.randomUUID(),
                "Kebun Joko",
                "Wonosobo",
                UUID.randomUUID(),
                "Andungsari",
                LocalDate.of(2026, 9, 30),
                new BigDecimal("40000"),
                new BigDecimal("15.000"),
                new BigDecimal("600000"),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        when(purchaseService.findAll())
                .thenReturn(List.of(purchase1, purchase2));

        mockMvc.perform(get("/api/v1/purchases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].supplierName").value("Pak Budi"))
                .andExpect(jsonPath("$[1].supplierName").value("Pak Joko"));

        verify(purchaseService).findAll();
    }
}