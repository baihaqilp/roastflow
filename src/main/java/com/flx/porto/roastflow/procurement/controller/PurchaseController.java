package com.flx.porto.roastflow.procurement.controller;

import com.flx.porto.roastflow.procurement.model.dto.CreatePurchaseRequest;
import com.flx.porto.roastflow.procurement.model.dto.PurchaseResponse;
import com.flx.porto.roastflow.procurement.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseResponse create(
            @Valid @RequestBody CreatePurchaseRequest request
    ) {
        return purchaseService.create(request);
    }

    @GetMapping
    public List<PurchaseResponse> findAll() {
        return purchaseService.findAll();
    }

    @GetMapping("/{id}")
    public PurchaseResponse findById(
            @PathVariable UUID id
    ) {
        return purchaseService.findById(id);
    }
}