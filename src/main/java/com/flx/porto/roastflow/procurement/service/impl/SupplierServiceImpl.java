package com.flx.porto.roastflow.procurement.service.impl;

import com.flx.porto.roastflow.common.exception.DuplicateResourceException;
import com.flx.porto.roastflow.common.exception.ResourceNotFoundException;
import com.flx.porto.roastflow.procurement.model.Supplier;
import com.flx.porto.roastflow.procurement.model.dto.CreateSupplierRequest;
import com.flx.porto.roastflow.procurement.model.dto.SupplierResponse;
import com.flx.porto.roastflow.procurement.repository.SupplierRepository;
import com.flx.porto.roastflow.procurement.service.SupplierService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public SupplierResponse create(CreateSupplierRequest request) {

        if (supplierRepository.existsByNameIgnoreCase(request.name())){
            throw new DuplicateResourceException(
                    "Supplier already exists: " + request.name()
            );
        }

        Supplier supplier = new Supplier(
                request.name(),
                request.phone(),
                request.address()
        );

        Supplier savedSupplier = supplierRepository.save(supplier);
        return SupplierResponse.from(savedSupplier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> findAll() {
        return supplierRepository.findAll()
                .stream()
                .map(SupplierResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse findById(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() ->
                            new ResourceNotFoundException("Supplier not found: "+ id)
                        );
        return SupplierResponse.from(supplier);
    }
}
