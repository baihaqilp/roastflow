package com.flx.porto.roastflow.procurement.service;

import com.flx.porto.roastflow.procurement.model.dto.CoffeeVarietyResponse;
import com.flx.porto.roastflow.procurement.model.dto.CreateCoffeeVarietyRequest;

import java.util.List;
import java.util.UUID;

public interface CoffeeVarietyService {
    CoffeeVarietyResponse create(CreateCoffeeVarietyRequest request);

    List<CoffeeVarietyResponse> findAll();

    CoffeeVarietyResponse findById(UUID id);
}
