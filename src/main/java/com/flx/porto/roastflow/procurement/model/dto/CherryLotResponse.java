package com.flx.porto.roastflow.procurement.model.dto;

import com.flx.porto.roastflow.procurement.model.CherryLot;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CherryLotResponse(
        UUID id,
        UUID purchaseId,
        String lotCode,
        BigDecimal initialWeightKg,
        BigDecimal remainingWeightKg,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static CherryLotResponse from(CherryLot lot) {
        return new CherryLotResponse(
                lot.getId(),
                lot.getPurchase().getId(),
                lot.getLotCode(),
                lot.getInitialWeightKg(),
                lot.getRemainingWeightKg(),
                lot.getStatus(),
                lot.getCreatedAt(),
                lot.getUpdatedAt()
        );
    }
}