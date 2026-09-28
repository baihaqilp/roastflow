package com.flx.porto.roastflow.procurement.model.dto;

import com.flx.porto.roastflow.procurement.model.Farm;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FarmResponse (
        UUID id,
        String name,
        String origin,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
){

    public static FarmResponse from(Farm farm){
        return new FarmResponse(
                farm.getId(),
                farm.getName(),
                farm.getOrigin(),
                farm.getCreatedAt(),
                farm.getUpdatedAt()
        );
    }
}
