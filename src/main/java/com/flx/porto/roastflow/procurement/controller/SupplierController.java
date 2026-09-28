package com.flx.porto.roastflow.procurement.controller;

import com.flx.porto.roastflow.procurement.model.dto.CreateSupplierRequest;
import com.flx.porto.roastflow.procurement.model.dto.SupplierResponse;
import com.flx.porto.roastflow.procurement.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SupplierResponse create(
            @Valid @RequestBody CreateSupplierRequest request
    ) {
        return supplierService.create(request);
    }

    @GetMapping
    public List<SupplierResponse> findAll() {
        return supplierService.findAll();
    }

    @GetMapping("/{id}")
    public SupplierResponse findById(
            @PathVariable UUID id
    ) {
        return supplierService.findById(id);
    }
}
