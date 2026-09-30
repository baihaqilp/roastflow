package com.flx.porto.roastflow.procurement.service.impl;

import com.flx.porto.roastflow.common.exception.ResourceNotFoundException;
import com.flx.porto.roastflow.procurement.model.CoffeeVariety;
import com.flx.porto.roastflow.procurement.model.Farm;
import com.flx.porto.roastflow.procurement.model.Purchase;
import com.flx.porto.roastflow.procurement.model.Supplier;
import com.flx.porto.roastflow.procurement.model.dto.CreatePurchaseRequest;
import com.flx.porto.roastflow.procurement.model.dto.PurchaseResponse;
import com.flx.porto.roastflow.procurement.repository.CoffeeVarietyRepository;
import com.flx.porto.roastflow.procurement.repository.FarmRepository;
import com.flx.porto.roastflow.procurement.repository.PurchaseRepository;
import com.flx.porto.roastflow.procurement.repository.SupplierRepository;
import com.flx.porto.roastflow.procurement.service.PurchaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final SupplierRepository supplierRepository;
    private final FarmRepository farmRepository;
    private final CoffeeVarietyRepository coffeeVarietyRepository;

    public PurchaseServiceImpl(
            PurchaseRepository purchaseRepository,
            SupplierRepository supplierRepository,
            FarmRepository farmRepository,
            CoffeeVarietyRepository coffeeVarietyRepository
    ) {
        this.purchaseRepository = purchaseRepository;
        this.supplierRepository = supplierRepository;
        this.farmRepository = farmRepository;
        this.coffeeVarietyRepository = coffeeVarietyRepository;
    }

    @Override
    public PurchaseResponse create(CreatePurchaseRequest request) {

        Supplier supplier = supplierRepository.findById(request.supplierId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Supplier not found: " + request.supplierId()
                        )
                );

        Farm farm = farmRepository.findById(request.farmId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Farm not found: " + request.farmId()
                        )
                );

        CoffeeVariety variety =
                coffeeVarietyRepository.findById(request.varietyId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Coffee variety not found: "
                                                + request.varietyId()
                                )
                        );

        Purchase purchase = new Purchase(
                supplier,
                farm,
                variety,
                request.purchaseDate(),
                request.pricePerKg(),
                request.totalWeightKg()
        );

        Purchase savedPurchase =
                purchaseRepository.save(purchase);

        return PurchaseResponse.from(savedPurchase);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseResponse> findAll() {

        return purchaseRepository.findAll()
                .stream()
                .map(PurchaseResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseResponse findById(UUID id) {

        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Purchase not found: " + id
                        )
                );

        return PurchaseResponse.from(purchase);
    }
}