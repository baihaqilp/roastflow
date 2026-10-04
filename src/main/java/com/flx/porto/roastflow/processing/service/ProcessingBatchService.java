package com.flx.porto.roastflow.processing.service;

import com.flx.porto.roastflow.processing.model.dto.CompleteProcessingBatchRequest;
import com.flx.porto.roastflow.processing.model.dto.CreateProcessingBatchRequest;
import com.flx.porto.roastflow.processing.model.dto.ProcessingBatchResponse;

import java.util.List;
import java.util.UUID;

public interface ProcessingBatchService {

    ProcessingBatchResponse create(CreateProcessingBatchRequest request);

    List<ProcessingBatchResponse> findAll();

    ProcessingBatchResponse findById(UUID id);

    ProcessingBatchResponse startProcessing(UUID id);

    ProcessingBatchResponse startDrying(UUID id);

    ProcessingBatchResponse complete(
            UUID id,
            CompleteProcessingBatchRequest request
    );
}