package com.flx.porto.roastflow.procurement.service.impl;

import com.flx.porto.roastflow.common.exception.DuplicateResourceException;
import com.flx.porto.roastflow.common.exception.ResourceNotFoundException;
import com.flx.porto.roastflow.procurement.model.Farm;
import com.flx.porto.roastflow.procurement.model.dto.CreateFarmRequest;
import com.flx.porto.roastflow.procurement.model.dto.FarmResponse;
import com.flx.porto.roastflow.procurement.repository.FarmRepository;
import com.flx.porto.roastflow.procurement.service.FarmService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FarmServiceImpl implements FarmService {

    private final FarmRepository farmRepository;

    public FarmServiceImpl(FarmRepository farmRepository) {
        this.farmRepository = farmRepository;
    }

    @Override
    public FarmResponse create(CreateFarmRequest request) {

        if (farmRepository.existByNameIgnoreCase(request.name())){
            throw new DuplicateResourceException(
                    "Farm already exist: " + request.name()
            );
        }

        Farm farm = new Farm(
                request.name(),
                request.origin()
        );

        Farm savedFarm = farmRepository.save(farm);

        return FarmResponse.from(savedFarm);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FarmResponse> findAll() {

        return farmRepository.findAll()
                .stream()
                .map(FarmResponse::from)
                .toList();
    }

    @Override
    public FarmResponse findById(UUID id) {

        Farm farm = farmRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Farm not found: "+ id
                        ));
        return FarmResponse.from(farm);
    }
}
