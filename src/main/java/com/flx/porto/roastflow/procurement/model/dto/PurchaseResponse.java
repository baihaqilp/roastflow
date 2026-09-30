package com.flx.porto.roastflow.procurement.model.dto;

import com.flx.porto.roastflow.procurement.model.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PurchaseResponse(
        UUID id,

        UUID supplierId,
        String supplierName,

        UUID farmId,
        String farmName,
        String farmOrigin,

        UUID varietyId,
        String varietyName,

        LocalDate purchaseDate,
        BigDecimal pricePerKg,
        BigDecimal totalWeightKg,
        BigDecimal totalAmount,

        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static PurchaseResponse from(Purchase purchase) {

        return new PurchaseResponse(
                purchase.getId(),

                purchase.getSupplier().getId(),
                purchase.getSupplier().getName(),

                purchase.getFarm().getId(),
                purchase.getFarm().getName(),
                purchase.getFarm().getOrigin(),

                purchase.getVariety().getId(),
                purchase.getVariety().getName(),

                purchase.getPurchaseDate(),
                purchase.getPricePerKg(),
                purchase.getTotalWeightKg(),
                purchase.getTotalAmount(),

                purchase.getCreatedAt(),
                purchase.getUpdatedAt()
        );
    }
}