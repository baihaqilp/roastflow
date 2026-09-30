package com.flx.porto.roastflow.procurement.service.impl;

import com.flx.porto.roastflow.common.exception.DuplicateResourceException;
import com.flx.porto.roastflow.common.exception.ResourceNotFoundException;
import com.flx.porto.roastflow.procurement.model.CoffeeVariety;
import com.flx.porto.roastflow.procurement.model.dto.CoffeeVarietyResponse;
import com.flx.porto.roastflow.procurement.model.dto.CreateCoffeeVarietyRequest;
import com.flx.porto.roastflow.procurement.repository.CoffeeVarietyRepository;
import com.flx.porto.roastflow.procurement.service.CoffeeVarietyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CoffeeVarietyServiceImpl implements CoffeeVarietyService {

    private final CoffeeVarietyRepository coffeeVarietyRepository;

    public CoffeeVarietyServiceImpl(
            CoffeeVarietyRepository coffeeVarietyRepository
    ) {
        this.coffeeVarietyRepository = coffeeVarietyRepository;
    }

    @Override
    public CoffeeVarietyResponse create(CreateCoffeeVarietyRequest request) {

        if (coffeeVarietyRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException(
                    "Coffee variety already exists: " + request.name()
            );
        }

        CoffeeVariety coffeeVariety =
                new CoffeeVariety(request.name());

        CoffeeVariety saved =
                coffeeVarietyRepository.save(coffeeVariety);

        return CoffeeVarietyResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoffeeVarietyResponse> findAll() {

        return coffeeVarietyRepository.findAll()
                .stream()
                .map(CoffeeVarietyResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CoffeeVarietyResponse findById(UUID id) {

        CoffeeVariety coffeeVariety =
                coffeeVarietyRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Coffee variety not found: " + id
                                )
                        );

        return CoffeeVarietyResponse.from(coffeeVariety);
    }
}