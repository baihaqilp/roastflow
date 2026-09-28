package com.flx.porto.roastflow.procurement.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFarmRequest(
        @NotBlank(message = "Farm name is required")
        @Size(
                max = 150,
                message = "Farm name must not exceed 150 characters"
        )
        String name,

        @NotBlank(message = "Origin is required")
        @Size(
                max = 200,
                message = "Origin must not exceed 200 characters"
        )
        String origin
) {

}
