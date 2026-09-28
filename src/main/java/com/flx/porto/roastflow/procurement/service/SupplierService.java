package com.flx.porto.roastflow.procurement.service;

import com.flx.porto.roastflow.procurement.model.dto.CreateSupplierRequest;
import com.flx.porto.roastflow.procurement.model.dto.SupplierResponse;

import java.util.List;
import java.util.UUID;

public interface SupplierService {

    SupplierResponse create(CreateSupplierRequest request);

    List<SupplierResponse> findAll();

    SupplierResponse findById(UUID id);
}
