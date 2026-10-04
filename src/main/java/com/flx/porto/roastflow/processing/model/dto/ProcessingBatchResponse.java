package com.flx.porto.roastflow.processing.model.dto;

import com.flx.porto.roastflow.processing.common.ProcessingMethod;
import com.flx.porto.roastflow.processing.common.ProcessingStatus;
import com.flx.porto.roastflow.processing.model.ProcessingBatch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ProcessingBatchResponse(
        UUID id,
        UUID cherryLotId,
        String batchCode,
        ProcessingMethod processingMethod,
        BigDecimal inputWeightKg,
        LocalDate startDate,
        LocalDate dryingStartDate,
        LocalDate dryingEndDate,
        BigDecimal outputGreenBeanKg,
        ProcessingStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static ProcessingBatchResponse from(
            ProcessingBatch batch
    ) {
        return new ProcessingBatchResponse(
                batch.getId(),
                batch.getCherryLot().getId(),
                batch.getBatchCode(),
                batch.getProcessingMethod(),
                batch.getInputWeightKg(),
                batch.getStartDate(),
                batch.getDryingStartDate(),
                batch.getDryingEndDate(),
                batch.getOutputGreenBeanKg(),
                batch.getStatus(),
                batch.getCreatedAt(),
                batch.getUpdatedAt()
        );
    }
}