package com.flx.porto.roastflow.procurement.controller;

import com.flx.porto.roastflow.common.exception.DuplicateResourceException;
import com.flx.porto.roastflow.common.exception.GlobalExceptionHandler;
import com.flx.porto.roastflow.common.exception.ResourceNotFoundException;
import com.flx.porto.roastflow.procurement.model.dto.CreateSupplierRequest;
import com.flx.porto.roastflow.procurement.model.dto.SupplierResponse;
import com.flx.porto.roastflow.procurement.service.SupplierService;


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

@WebMvcTest(SupplierController.class)
@Import(GlobalExceptionHandler.class)
class SupplierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SupplierService supplierService;


    @Test
    void shouldCreateSupplierSuccessfully() throws Exception {

        CreateSupplierRequest request =
                new CreateSupplierRequest(
                        "Pak Budi",
                        "081234567890",
                        "Banjarnegara, Jawa Tengah"
                );

        UUID supplierId = UUID.randomUUID();

        SupplierResponse response =
                new SupplierResponse(
                        supplierId,
                        "Pak Budi",
                        "081234567890",
                        "Banjarnegara, Jawa Tengah",
                        OffsetDateTime.now(),
                        OffsetDateTime.now()
                );

        when(supplierService.create(any(CreateSupplierRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/suppliers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(supplierId.toString()))
                .andExpect(jsonPath("$.name").value("Pak Budi"))
                .andExpect(jsonPath("$.phone").value("081234567890"))
                .andExpect(jsonPath("$.address")
                        .value("Banjarnegara, Jawa Tengah"));
    }


    @Test
    void shouldReturnBadRequestWhenSupplierNameIsBlank()
            throws Exception {

        CreateSupplierRequest request =
                new CreateSupplierRequest(
                        "",
                        "081234567890",
                        "Banjarnegara"
                );

        mockMvc.perform(
                        post("/api/v1/suppliers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("name: Supplier name is required"))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/suppliers"));
    }


    @Test
    void shouldReturnConflictWhenSupplierAlreadyExists()
            throws Exception {

        CreateSupplierRequest request =
                new CreateSupplierRequest(
                        "Pak Budi",
                        "081234567890",
                        "Banjarnegara"
                );

        when(supplierService.create(any(CreateSupplierRequest.class)))
                .thenThrow(
                        new DuplicateResourceException(
                                "Supplier already exists: Pak Budi"
                        )
                );

        mockMvc.perform(
                        post("/api/v1/suppliers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error")
                        .value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("Supplier already exists: Pak Budi"))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/suppliers"));
    }


    @Test
    void shouldFindSupplierById() throws Exception {

        UUID supplierId = UUID.randomUUID();

        SupplierResponse response =
                new SupplierResponse(
                        supplierId,
                        "Pak Budi",
                        "081234567890",
                        "Banjarnegara",
                        OffsetDateTime.now(),
                        OffsetDateTime.now()
                );

        when(supplierService.findById(supplierId))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/suppliers/{id}", supplierId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(supplierId.toString()))
                .andExpect(jsonPath("$.name")
                        .value("Pak Budi"))
                .andExpect(jsonPath("$.phone")
                        .value("081234567890"))
                .andExpect(jsonPath("$.address")
                        .value("Banjarnegara"));
    }


    @Test
    void shouldReturnNotFoundWhenSupplierDoesNotExist()
            throws Exception {

        UUID supplierId = UUID.randomUUID();

        when(supplierService.findById(supplierId))
                .thenThrow(
                        new ResourceNotFoundException(
                                "Supplier not found: " + supplierId
                        )
                );

        mockMvc.perform(
                        get("/api/v1/suppliers/{id}", supplierId)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error")
                        .value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Supplier not found: " + supplierId))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/suppliers/" + supplierId));
    }


    @Test
    void shouldFindAllSuppliers() throws Exception {

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        SupplierResponse supplier1 =
                new SupplierResponse(
                        id1,
                        "Pak Budi",
                        "081234567890",
                        "Banjarnegara",
                        OffsetDateTime.now(),
                        OffsetDateTime.now()
                );

        SupplierResponse supplier2 =
                new SupplierResponse(
                        id2,
                        "Pak Joko",
                        "082234567890",
                        "Wonosobo",
                        OffsetDateTime.now(),
                        OffsetDateTime.now()
                );

        when(supplierService.findAll())
                .thenReturn(List.of(supplier1, supplier2));

        mockMvc.perform(
                        get("/api/v1/suppliers")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                        .andExpect(jsonPath("$[0].name")
                                .value("Pak Budi"))
                        .andExpect(jsonPath("$[1].name")
                                .value("Pak Joko"));
    }
}