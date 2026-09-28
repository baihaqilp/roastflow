package com.flx.porto.roastflow.procurement.model.dto;

import com.flx.porto.roastflow.procurement.model.Supplier;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SupplierResponse (
        UUID id,
        String name,
        String phone,
        String address,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
){

    public static  SupplierResponse from(Supplier supplier){
        return new SupplierResponse(
                supplier.getId(),
                supplier.getName(),
                supplier.getPhone(),
                supplier.getAddress(),
                supplier.getCreatedAt(),
                supplier.getUpdatedAt()
        );
    }
}

