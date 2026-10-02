package com.flx.porto.roastflow.procurement.service;

import com.flx.porto.roastflow.procurement.model.dto.CherryLotResponse;
import com.flx.porto.roastflow.procurement.model.dto.CreateCherryLotRequest;

import java.util.List;
import java.util.UUID;

public interface CherryLotService {

    CherryLotResponse create(CreateCherryLotRequest request);

    List<CherryLotResponse> findAll();

    CherryLotResponse findById(UUID id);
}