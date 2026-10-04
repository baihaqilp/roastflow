package com.flx.porto.roastflow.processing.model.dto;

import com.flx.porto.roastflow.processing.common.ProcessingMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateProcessingBatchRequest(

        @NotNull
        UUID cherryLotId,

        @NotBlank
        String batchCode,

        @NotNull
        ProcessingMethod processingMethod,

        @NotNull
        @DecimalMin(
                value = "0.001",
                message = "Input weight must be greater than zero"
        )
        BigDecimal inputWeightKg,

        @NotNull
        LocalDate startDate
) {
}
