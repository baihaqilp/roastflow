package com.flx.porto.roastflow.procurement.controller;

import com.flx.porto.roastflow.procurement.model.dto.CherryLotResponse;
import com.flx.porto.roastflow.procurement.model.dto.CreateCherryLotRequest;
import com.flx.porto.roastflow.procurement.service.CherryLotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cherry-lots")
public class CherryLotController {

    private final CherryLotService cherryLotService;

    public CherryLotController(CherryLotService cherryLotService) {
        this.cherryLotService = cherryLotService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CherryLotResponse create(
            @Valid @RequestBody CreateCherryLotRequest request
    ) {
        return cherryLotService.create(request);
    }

    @GetMapping
    public List<CherryLotResponse> findAll() {
        return cherryLotService.findAll();
    }

    @GetMapping("/{id}")
    public CherryLotResponse findById(@PathVariable UUID id) {
        return cherryLotService.findById(id);
    }
}