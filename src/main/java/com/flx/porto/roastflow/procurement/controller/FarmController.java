package com.flx.porto.roastflow.procurement.controller;

import com.flx.porto.roastflow.procurement.model.dto.CreateFarmRequest;
import com.flx.porto.roastflow.procurement.model.dto.FarmResponse;
import com.flx.porto.roastflow.procurement.service.FarmService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/farms")
public class FarmController {

    private final FarmService farmService;

    public FarmController(FarmService farmService) {
        this.farmService = farmService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FarmResponse creae(
            @Valid @RequestBody CreateFarmRequest request
            ){
        return farmService.create(request);
    }

    @GetMapping
    public List<FarmResponse> findAll(){
        return farmService.findAll();
    }

    @GetMapping("/{id}")
    public FarmResponse findById(@PathVariable UUID id){
        return farmService.findById(id);
    }
}
