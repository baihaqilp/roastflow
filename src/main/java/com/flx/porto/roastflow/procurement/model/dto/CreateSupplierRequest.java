package com.flx.porto.roastflow.procurement.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSupplierRequest(
        @NotBlank(message = "Supplier name is required")
        @Size(max=150, message = "Supplier name must not exceed 150 characters")
        String name,

        @Size(max = 30 , message = "Phone must not exceed 30 charactees")
        String phone,

        String address
) {

}
