package com.flx.porto.roastflow.procurement.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreatePurchaseRequest(

        @NotNull(message = "Supplier ID is required")
        UUID supplierId,

        @NotNull(message = "Farm ID is required")
        UUID farmId,

        @NotNull(message = "Coffee variety ID is required")
        UUID varietyId,

        @NotNull(message = "Purchase date is required")
        LocalDate purchaseDate,

        @NotNull(message = "Price per kg is required")
        @DecimalMin(
                value = "0.01",
                message = "Price per kg must be greater than zero"
        )
        BigDecimal pricePerKg,

        @NotNull(message = "Total weight is required")
        @DecimalMin(
                value = "0.001",
                message = "Total weight must be greater than zero"
        )
        BigDecimal totalWeightKg

) {
}