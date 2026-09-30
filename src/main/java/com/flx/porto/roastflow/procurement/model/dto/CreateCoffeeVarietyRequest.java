package com.flx.porto.roastflow.procurement.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCoffeeVarietyRequest(
        @NotBlank(message = "Coffee variety name is required")
        @Size(max = 100, message = "Coffee variety name must not exceed 100 characters")
        String name
) {
}
