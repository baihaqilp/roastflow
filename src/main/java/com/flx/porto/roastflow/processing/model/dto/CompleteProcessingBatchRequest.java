package com.flx.porto.roastflow.processing.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CompleteProcessingBatchRequest(

        @NotNull
        @DecimalMin(
                value = "0.001",
                message = "Output green bean weight must be greater than zero"
        )
        BigDecimal outputGreenBeanKg,

        @NotNull
        LocalDate dryingEndDate
) {
}