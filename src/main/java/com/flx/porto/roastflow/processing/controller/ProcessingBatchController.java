package com.flx.porto.roastflow.processing.controller;

import com.flx.porto.roastflow.processing.model.dto.CompleteProcessingBatchRequest;
import com.flx.porto.roastflow.processing.model.dto.CreateProcessingBatchRequest;
import com.flx.porto.roastflow.processing.model.dto.ProcessingBatchResponse;
import com.flx.porto.roastflow.processing.service.ProcessingBatchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/processing-batches")
public class ProcessingBatchController {

    private final ProcessingBatchService processingBatchService;

    public ProcessingBatchController(
            ProcessingBatchService processingBatchService
    ) {
        this.processingBatchService = processingBatchService;
    }

    @PostMapping
    public ResponseEntity<ProcessingBatchResponse> create(
            @Valid @RequestBody CreateProcessingBatchRequest request
    ) {
        ProcessingBatchResponse response =
                processingBatchService.create(request);

        URI location = URI.create(
                "/api/v1/processing-batches/" + response.id()
        );

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProcessingBatchResponse>> findAll() {

        return ResponseEntity.ok(
                processingBatchService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcessingBatchResponse> findById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                processingBatchService.findById(id)
        );
    }

    //Processing Batch Lifecycle

    @PostMapping("/{id}/start")
    public ResponseEntity<ProcessingBatchResponse> startProcessing(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                processingBatchService.startProcessing(id)
        );
    }

    @PostMapping("/{id}/start-drying")
    public ResponseEntity<ProcessingBatchResponse> startDrying(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                processingBatchService.startDrying(id)
        );
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<ProcessingBatchResponse> complete(
            @PathVariable UUID id,
            @Valid @RequestBody CompleteProcessingBatchRequest request
    ) {
        return ResponseEntity.ok(
                processingBatchService.complete(id, request)
        );
    }
}