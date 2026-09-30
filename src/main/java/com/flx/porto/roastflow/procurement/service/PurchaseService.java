package com.flx.porto.roastflow.procurement.service;

import com.flx.porto.roastflow.procurement.model.dto.CreatePurchaseRequest;
import com.flx.porto.roastflow.procurement.model.dto.PurchaseResponse;

import java.util.List;
import java.util.UUID;

public interface PurchaseService {

    PurchaseResponse create(CreatePurchaseRequest request);

    List<PurchaseResponse> findAll();

    PurchaseResponse findById(UUID id);
}