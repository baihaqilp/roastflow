package com.flx.porto.roastflow.procurement.service.impl;

import com.flx.porto.roastflow.common.exception.DuplicateResourceException;
import com.flx.porto.roastflow.common.exception.ResourceNotFoundException;
import com.flx.porto.roastflow.procurement.model.CherryLot;
import com.flx.porto.roastflow.procurement.model.Purchase;
import com.flx.porto.roastflow.procurement.model.dto.CherryLotResponse;
import com.flx.porto.roastflow.procurement.model.dto.CreateCherryLotRequest;
import com.flx.porto.roastflow.procurement.repository.CherryLotRepository;
import com.flx.porto.roastflow.procurement.repository.PurchaseRepository;
import com.flx.porto.roastflow.procurement.service.CherryLotService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CherryLotServiceImpl implements CherryLotService {

    private final CherryLotRepository cherryLotRepository;
    private final PurchaseRepository purchaseRepository;

    public CherryLotServiceImpl(
            CherryLotRepository cherryLotRepository,
            PurchaseRepository purchaseRepository
    ) {
        this.cherryLotRepository = cherryLotRepository;
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public CherryLotResponse create(CreateCherryLotRequest request) {

        if (cherryLotRepository.existsByLotCodeIgnoreCase(request.lotCode())) {
            throw new DuplicateResourceException(
                    "Cherry lot with code '" + request.lotCode() + "' already exists"
            );
        }

        Purchase purchase = purchaseRepository.findById(request.purchaseId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Purchase not found with id: " + request.purchaseId()
                        )
                );

        if (request.initialWeightKg()
                .compareTo(purchase.getTotalWeightKg()) > 0) {

            throw new IllegalArgumentException(
                    "Cherry lot weight cannot exceed purchase weight"
            );
        }

        CherryLot cherryLot = new CherryLot(
                purchase,
                request.lotCode(),
                request.initialWeightKg()
        );

        CherryLot saved = cherryLotRepository.save(cherryLot);

        return CherryLotResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CherryLotResponse> findAll() {
        return cherryLotRepository.findAll()
                .stream()
                .map(CherryLotResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CherryLotResponse findById(UUID id) {
        CherryLot cherryLot = cherryLotRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cherry lot not found with id: " + id
                        )
                );

        return CherryLotResponse.from(cherryLot);
    }
}