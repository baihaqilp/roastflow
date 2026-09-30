package com.flx.porto.roastflow.procurement.model.dto;

import com.flx.porto.roastflow.procurement.model.CoffeeVariety;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CoffeeVarietyResponse(
        UUID id,
        String name,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static CoffeeVarietyResponse from(CoffeeVariety coffeeVariety){
        return new CoffeeVarietyResponse(
                coffeeVariety.getId(),
                coffeeVariety.getName(),
                coffeeVariety.getCreatedAt(),
                coffeeVariety.getUpdatedAt()

        );
    }
}
