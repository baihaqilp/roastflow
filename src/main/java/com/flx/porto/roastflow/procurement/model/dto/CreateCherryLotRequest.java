package com.flx.porto.roastflow.procurement.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateCherryLotRequest(

        @NotNull
        UUID purchaseId,

        @NotBlank
        String lotCode,

        @NotNull
        @DecimalMin(
                value = "0.001",
                message = "Initial weight must be greater than zero"
        )
        BigDecimal initialWeightKg
) {
}