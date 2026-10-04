package com.flx.porto.roastflow.processing.model;

import com.flx.porto.roastflow.processing.common.ProcessingMethod;
import com.flx.porto.roastflow.processing.common.ProcessingStatus;
import com.flx.porto.roastflow.procurement.model.CherryLot;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "processing_batches",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_processing_batches_batch_code",
                        columnNames = "batch_code"
                )
        }
)
public class ProcessingBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cherry_lot_id", nullable = false)
    private CherryLot cherryLot;

    @Column(name = "batch_code", nullable = false, length = 50)
    private String batchCode;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "processing_method",
            nullable = false,
            length = 30
    )
    private ProcessingMethod processingMethod;

    @Column(
            name = "input_weight_kg",
            nullable = false,
            precision = 15,
            scale = 3
    )
    private BigDecimal inputWeightKg;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "drying_start_date")
    private LocalDate dryingStartDate;

    @Column(name = "drying_end_date")
    private LocalDate dryingEndDate;

    @Column(name = "output_green_bean_kg", precision = 15, scale = 3)
    private BigDecimal outputGreenBeanKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProcessingStatus status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected ProcessingBatch() {
    }

    public ProcessingBatch(
            CherryLot cherryLot,
            String batchCode,
            ProcessingMethod processingMethod,
            BigDecimal inputWeightKg,
            LocalDate startDate
    ) {
        this.cherryLot = cherryLot;
        this.batchCode = batchCode;
        this.processingMethod = processingMethod;
        this.inputWeightKg = inputWeightKg;
        this.startDate = startDate;
        this.status = ProcessingStatus.PLANNED;
    }

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public CherryLot getCherryLot() {
        return cherryLot;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public ProcessingMethod getProcessingMethod() {
        return processingMethod;
    }

    public BigDecimal getInputWeightKg() {
        return inputWeightKg;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getDryingStartDate() {
        return dryingStartDate;
    }

    public LocalDate getDryingEndDate() {
        return dryingEndDate;
    }

    public BigDecimal getOutputGreenBeanKg() {
        return outputGreenBeanKg;
    }

    public ProcessingStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void startProcessing() {

        if (status != ProcessingStatus.PLANNED) {
            throw new IllegalStateException(
                    "Processing batch can only start from PLANNED status"
            );
        }

        this.status = ProcessingStatus.PROCESSING;
    }

    public void startDrying(LocalDate dryingStartDate) {

        if (status != ProcessingStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Drying can only start when processing batch is PROCESSING"
            );
        }

        this.dryingStartDate = dryingStartDate;
        this.status = ProcessingStatus.DRYING;
    }

    public void complete(
            LocalDate dryingEndDate,
            BigDecimal outputGreenBeanKg
    ) {

        if (status != ProcessingStatus.DRYING) {
            throw new IllegalStateException(
                    "Processing batch can only be completed from DRYING status"
            );
        }

        if (dryingStartDate == null) {
            throw new IllegalStateException(
                    "Drying start date is required"
            );
        }

        if (dryingEndDate.isBefore(dryingStartDate)) {
            throw new IllegalArgumentException(
                    "Drying end date cannot be before drying start date"
            );
        }

        if (outputGreenBeanKg.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Output green bean weight must be greater than zero"
            );
        }

        if (outputGreenBeanKg.compareTo(inputWeightKg) > 0) {
            throw new IllegalArgumentException(
                    "Output green bean weight cannot exceed input weight"
            );
        }

        this.dryingEndDate = dryingEndDate;
        this.outputGreenBeanKg = outputGreenBeanKg;
        this.status = ProcessingStatus.COMPLETED;
    }
}