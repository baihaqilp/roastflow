package com.flx.porto.roastflow.procurement.controller;

import com.flx.porto.roastflow.procurement.model.dto.CoffeeVarietyResponse;
import com.flx.porto.roastflow.procurement.model.dto.CreateCoffeeVarietyRequest;
import com.flx.porto.roastflow.procurement.service.CoffeeVarietyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/coffee-varieties")
public class CoffeeVarietyController {

    private final CoffeeVarietyService coffeeVarietyService;

    public CoffeeVarietyController(
            CoffeeVarietyService coffeeVarietyService
    ) {
        this.coffeeVarietyService = coffeeVarietyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CoffeeVarietyResponse create(
            @Valid @RequestBody CreateCoffeeVarietyRequest request
    ) {
        return coffeeVarietyService.create(request);
    }

    @GetMapping
    public List<CoffeeVarietyResponse> findAll() {
        return coffeeVarietyService.findAll();
    }

    @GetMapping("/{id}")
    public CoffeeVarietyResponse findById(
            @PathVariable UUID id
    ) {
        return coffeeVarietyService.findById(id);
    }
}