package com.flx.porto.roastflow.processing.controller;

import com.flx.porto.roastflow.processing.common.ProcessingMethod;
import com.flx.porto.roastflow.processing.common.ProcessingStatus;
import com.flx.porto.roastflow.processing.model.dto.CompleteProcessingBatchRequest;
import com.flx.porto.roastflow.processing.model.dto.ProcessingBatchResponse;
import com.flx.porto.roastflow.processing.service.ProcessingBatchService;
import com.flx.porto.roastflow.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProcessingBatchController.class)
@Import(GlobalExceptionHandler.class)
class ProcessingBatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProcessingBatchService processingBatchService;

    @Test
    void shouldStartProcessing() throws Exception {
        UUID id = UUID.randomUUID();

        ProcessingBatchResponse response = new ProcessingBatchResponse(
                id,
                UUID.randomUUID(),
                "PB-2026-001",
                ProcessingMethod.NATURAL,
                new BigDecimal("20.000"),
                LocalDate.of(2026, 10, 1),
                null,
                null,
                null,
                ProcessingStatus.PROCESSING,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        when(processingBatchService.startProcessing(id))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/processing-batches/{id}/start", id)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.batchCode").value("PB-2026-001"))
                .andExpect(jsonPath("$.status").value("PROCESSING"));
    }

    @Test
    void shouldStartDrying() throws Exception {
        UUID id = UUID.randomUUID();

        ProcessingBatchResponse response = new ProcessingBatchResponse(
                id,
                UUID.randomUUID(),
                "PB-2026-001",
                ProcessingMethod.NATURAL,
                new BigDecimal("20.000"),
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 2),
                null,
                null,
                ProcessingStatus.DRYING,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        when(processingBatchService.startDrying(id))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/processing-batches/{id}/start-drying", id)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("DRYING"))
                .andExpect(jsonPath("$.dryingStartDate").value("2026-10-02"));
    }

    @Test
    void shouldCompleteProcessing() throws Exception {
        UUID id = UUID.randomUUID();

        ProcessingBatchResponse response = new ProcessingBatchResponse(
                id,
                UUID.randomUUID(),
                "PB-2026-001",
                ProcessingMethod.NATURAL,
                new BigDecimal("20.000"),
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 2),
                LocalDate.of(2026, 10, 25),
                new BigDecimal("4.000"),
                ProcessingStatus.COMPLETED,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        when(processingBatchService.complete(
                org.mockito.ArgumentMatchers.eq(id),
                org.mockito.ArgumentMatchers.any(CompleteProcessingBatchRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/processing-batches/{id}/complete", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "outputGreenBeanKg": 4.000,
                                          "dryingEndDate": "2026-10-25"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.outputGreenBeanKg").value(4.000))
                .andExpect(jsonPath("$.dryingEndDate").value("2026-10-25"));
    }

    @Test
    void shouldRejectCompleteRequestWhenOutputWeightIsMissing() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(
                        post("/api/v1/processing-batches/{id}/complete", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "dryingEndDate": "2026-10-25"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCompleteRequestWhenDryingEndDateIsMissing() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(
                        post("/api/v1/processing-batches/{id}/complete", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "outputGreenBeanKg": 4.000
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }
}