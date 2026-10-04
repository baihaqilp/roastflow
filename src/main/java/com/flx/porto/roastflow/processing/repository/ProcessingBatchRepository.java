package com.flx.porto.roastflow.processing.repository;

import com.flx.porto.roastflow.processing.model.ProcessingBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessingBatchRepository
        extends JpaRepository<ProcessingBatch, UUID> {

    boolean existsByBatchCodeIgnoreCase(String batchCode);
}