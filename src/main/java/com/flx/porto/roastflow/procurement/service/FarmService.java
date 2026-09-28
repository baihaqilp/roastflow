package com.flx.porto.roastflow.procurement.service;

import com.flx.porto.roastflow.procurement.model.dto.CreateFarmRequest;
import com.flx.porto.roastflow.procurement.model.dto.FarmResponse;

import java.util.List;
import java.util.UUID;

public interface FarmService {

    FarmResponse create(CreateFarmRequest request);

    List<FarmResponse> findAll();

    FarmResponse findById(UUID id);
}
