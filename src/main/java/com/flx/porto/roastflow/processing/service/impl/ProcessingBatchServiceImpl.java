package com.flx.porto.roastflow.processing.service.impl;

import com.flx.porto.roastflow.common.exception.DuplicateResourceException;
import com.flx.porto.roastflow.common.exception.ResourceNotFoundException;
import com.flx.porto.roastflow.processing.model.ProcessingBatch;
import com.flx.porto.roastflow.processing.model.dto.CompleteProcessingBatchRequest;
import com.flx.porto.roastflow.processing.model.dto.CreateProcessingBatchRequest;
import com.flx.porto.roastflow.processing.model.dto.ProcessingBatchResponse;
import com.flx.porto.roastflow.processing.repository.ProcessingBatchRepository;
import com.flx.porto.roastflow.processing.service.ProcessingBatchService;
import com.flx.porto.roastflow.procurement.model.CherryLot;
import com.flx.porto.roastflow.procurement.repository.CherryLotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProcessingBatchServiceImpl implements ProcessingBatchService {

    private final ProcessingBatchRepository processingBatchRepository;
    private final CherryLotRepository cherryLotRepository;

    public ProcessingBatchServiceImpl(
            ProcessingBatchRepository processingBatchRepository,
            CherryLotRepository cherryLotRepository
    ) {
        this.processingBatchRepository = processingBatchRepository;
        this.cherryLotRepository = cherryLotRepository;
    }

    @Override
    public ProcessingBatchResponse create(CreateProcessingBatchRequest request) {

        if (processingBatchRepository.existsByBatchCodeIgnoreCase(request.batchCode())) {
            throw new DuplicateResourceException(
                    "Processing batch code already exists: " + request.batchCode()
            );
        }

        CherryLot cherryLot = cherryLotRepository.findById(request.cherryLotId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cherry lot not found: " + request.cherryLotId()
                ));

        if (request.inputWeightKg().compareTo(cherryLot.getRemainingWeightKg()) > 0) {
            throw new IllegalArgumentException(
                    "Input weight cannot exceed remaining cherry lot weight"
            );
        }

        ProcessingBatch processingBatch = new ProcessingBatch(
                cherryLot,
                request.batchCode(),
                request.processingMethod(),
                request.inputWeightKg(),
                request.startDate()
        );

        ProcessingBatch saved = processingBatchRepository.save(processingBatch);

        return ProcessingBatchResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessingBatchResponse> findAll() {

        return processingBatchRepository.findAll()
                .stream()
                .map(ProcessingBatchResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProcessingBatchResponse findById(UUID id) {

        ProcessingBatch processingBatch = processingBatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Processing batch not found: " + id
                ));

        return ProcessingBatchResponse.from(processingBatch);
    }

    @Override
    public ProcessingBatchResponse startProcessing(UUID id) {

        ProcessingBatch batch = processingBatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Processing batch not found: " + id
                ));

        batch.startProcessing();

        ProcessingBatch saved =
                processingBatchRepository.save(batch);

        return ProcessingBatchResponse.from(saved);
    }

    @Override
    public ProcessingBatchResponse startDrying(UUID id) {

        ProcessingBatch batch = processingBatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Processing batch not found: " + id
                ));

        batch.startDrying(LocalDate.now());

        ProcessingBatch saved =
                processingBatchRepository.save(batch);

        return ProcessingBatchResponse.from(saved);
    }
    @Override
    public ProcessingBatchResponse complete(
            UUID id,
            CompleteProcessingBatchRequest request
    ) {

        ProcessingBatch batch = processingBatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Processing batch not found: " + id
                ));

        batch.complete(
                request.dryingEndDate(),
                request.outputGreenBeanKg()
        );

        ProcessingBatch saved =
                processingBatchRepository.save(batch);

        return ProcessingBatchResponse.from(saved);
    }
}