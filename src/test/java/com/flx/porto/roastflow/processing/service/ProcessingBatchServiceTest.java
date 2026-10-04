package com.flx.porto.roastflow.processing.service;

import com.flx.porto.roastflow.common.exception.DuplicateResourceException;
import com.flx.porto.roastflow.common.exception.ResourceNotFoundException;
import com.flx.porto.roastflow.processing.common.ProcessingMethod;
import com.flx.porto.roastflow.processing.common.ProcessingStatus;
import com.flx.porto.roastflow.processing.model.ProcessingBatch;
import com.flx.porto.roastflow.processing.model.dto.CompleteProcessingBatchRequest;
import com.flx.porto.roastflow.processing.model.dto.CreateProcessingBatchRequest;
import com.flx.porto.roastflow.processing.model.dto.ProcessingBatchResponse;
import com.flx.porto.roastflow.processing.repository.ProcessingBatchRepository;
import com.flx.porto.roastflow.processing.service.impl.ProcessingBatchServiceImpl;
import com.flx.porto.roastflow.procurement.model.CherryLot;
import com.flx.porto.roastflow.procurement.repository.CherryLotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessingBatchServiceTest {

    @Mock
    private ProcessingBatchRepository processingBatchRepository;

    @Mock
    private CherryLotRepository cherryLotRepository;

    @InjectMocks
    private ProcessingBatchServiceImpl processingBatchService;

    private CherryLot cherryLot;

    @BeforeEach
    void setUp() {

        cherryLot = mock(CherryLot.class);
    }

    @Test
    void shouldCreateProcessingBatchSuccessfully() {

        UUID cherryLotId = UUID.randomUUID();

        when(cherryLot.getId())
                .thenReturn(cherryLotId);

        when(cherryLot.getRemainingWeightKg())
                .thenReturn(new BigDecimal("50.000"));

        CreateProcessingBatchRequest request =
                new CreateProcessingBatchRequest(
                        cherryLotId,
                        "PB-2026-001",
                        ProcessingMethod.NATURAL,
                        new BigDecimal("20.000"),
                        LocalDate.of(2026, 10, 1)
                );

        when(processingBatchRepository.existsByBatchCodeIgnoreCase("PB-2026-001"))
                .thenReturn(false);

        when(cherryLotRepository.findById(cherryLotId))
                .thenReturn(Optional.of(cherryLot));

        ProcessingBatch savedBatch = new ProcessingBatch(
                cherryLot,
                "PB-2026-001",
                ProcessingMethod.NATURAL,
                new BigDecimal("20.000"),
                LocalDate.of(2026, 10, 1)
        );

        when(processingBatchRepository.save(any(ProcessingBatch.class)))
                .thenReturn(savedBatch);

        ProcessingBatchResponse response =
                processingBatchService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.batchCode()).isEqualTo("PB-2026-001");
        assertThat(response.processingMethod())
                .isEqualTo(ProcessingMethod.NATURAL);
        assertThat(response.inputWeightKg())
                .isEqualByComparingTo("20.000");

        verify(processingBatchRepository)
                .existsByBatchCodeIgnoreCase("PB-2026-001");

        verify(cherryLotRepository)
                .findById(cherryLotId);

        verify(processingBatchRepository)
                .save(any(ProcessingBatch.class));
    }

    @Test
    void shouldRejectDuplicateBatchCode() {

        CreateProcessingBatchRequest request =
                new CreateProcessingBatchRequest(
                        cherryLot.getId(),
                        "PB-2026-001",
                        ProcessingMethod.NATURAL,
                        new BigDecimal("20.000"),
                        LocalDate.of(2026, 10, 1)
                );

        when(processingBatchRepository.existsByBatchCodeIgnoreCase("PB-2026-001"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                processingBatchService.create(request)
        )
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("PB-2026-001");

        verify(processingBatchRepository, never())
                .save(any());
    }

    @Test
    void shouldRejectWhenCherryLotNotFound() {

        UUID cherryLotId = UUID.randomUUID();

        CreateProcessingBatchRequest request =
                new CreateProcessingBatchRequest(
                        cherryLotId,
                        "PB-2026-002",
                        ProcessingMethod.NATURAL,
                        new BigDecimal("20.000"),
                        LocalDate.of(2026, 10, 1)
                );

        when(processingBatchRepository.existsByBatchCodeIgnoreCase("PB-2026-002"))
                .thenReturn(false);

        when(cherryLotRepository.findById(cherryLotId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                processingBatchService.create(request)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(cherryLotId.toString());

        verify(processingBatchRepository, never())
                .save(any());
    }

    @Test
    void shouldRejectInputWeightGreaterThanRemainingCherryWeight() {

        UUID cherryLotId = UUID.randomUUID();

        when(cherryLot.getRemainingWeightKg())
                .thenReturn(new BigDecimal("50.000"));

        CreateProcessingBatchRequest request =
                new CreateProcessingBatchRequest(
                        cherryLotId,
                        "PB-2026-003",
                        ProcessingMethod.NATURAL,
                        new BigDecimal("60.000"),
                        LocalDate.of(2026, 10, 1)
                );

        when(processingBatchRepository
                .existsByBatchCodeIgnoreCase("PB-2026-003"))
                .thenReturn(false);

        when(cherryLotRepository.findById(cherryLotId))
                .thenReturn(Optional.of(cherryLot));

        assertThatThrownBy(() ->
                processingBatchService.create(request)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(
                        "Input weight cannot exceed"
                );

        verify(processingBatchRepository, never())
                .save(any());
    }

    @Test
    void shouldFindProcessingBatchById() {

        UUID batchId = UUID.randomUUID();

        ProcessingBatch batch = new ProcessingBatch(
                cherryLot,
                "PB-2026-004",
                ProcessingMethod.HONEY,
                new BigDecimal("15.000"),
                LocalDate.of(2026, 10, 1)
        );

        when(processingBatchRepository.findById(batchId))
                .thenReturn(Optional.of(batch));

        ProcessingBatchResponse response =
                processingBatchService.findById(batchId);

        assertThat(response).isNotNull();
        assertThat(response.batchCode()).isEqualTo("PB-2026-004");
        assertThat(response.processingMethod())
                .isEqualTo(ProcessingMethod.HONEY);
    }

    @Test
    void shouldThrowWhenProcessingBatchNotFound() {

        UUID batchId = UUID.randomUUID();

        when(processingBatchRepository.findById(batchId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                processingBatchService.findById(batchId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(batchId.toString());
    }

    @Test
    void shouldStartProcessing() {

        UUID batchId = UUID.randomUUID();

        ProcessingBatch batch = new ProcessingBatch(
                cherryLot,
                "PB-2026-005",
                ProcessingMethod.NATURAL,
                new BigDecimal("20.000"),
                LocalDate.of(2026, 10, 1)
        );

        when(processingBatchRepository.findById(batchId))
                .thenReturn(Optional.of(batch));

        when(processingBatchRepository.save(batch))
                .thenReturn(batch);

        ProcessingBatchResponse response =
                processingBatchService.startProcessing(batchId);

        assertThat(batch.getStatus())
                .isEqualTo(ProcessingStatus.PROCESSING);

        verify(processingBatchRepository)
                .findById(batchId);

        verify(processingBatchRepository)
                .save(batch);
    }

    @Test
    void shouldStartDrying() {

        UUID batchId = UUID.randomUUID();

        ProcessingBatch batch = new ProcessingBatch(
                cherryLot,
                "PB-2026-006",
                ProcessingMethod.HONEY,
                new BigDecimal("20.000"),
                LocalDate.of(2026, 10, 1)
        );

        batch.startProcessing();

        when(processingBatchRepository.findById(batchId))
                .thenReturn(Optional.of(batch));

        when(processingBatchRepository.save(batch))
                .thenReturn(batch);

        ProcessingBatchResponse response =
                processingBatchService.startDrying(batchId);

        assertThat(batch.getStatus())
                .isEqualTo(ProcessingStatus.DRYING);

        assertThat(batch.getDryingStartDate())
                .isEqualTo(LocalDate.now());

        verify(processingBatchRepository)
                .findById(batchId);

        verify(processingBatchRepository)
                .save(batch);
    }

    @Test
    void shouldCompleteProcessing() {

        UUID batchId = UUID.randomUUID();

        ProcessingBatch batch = new ProcessingBatch(
                cherryLot,
                "PB-2026-007",
                ProcessingMethod.WASHED,
                new BigDecimal("20.000"),
                LocalDate.of(2026, 10, 1)
        );

        batch.startProcessing();

        batch.startDrying(
                LocalDate.of(2026, 10, 5)
        );

        when(processingBatchRepository.findById(batchId))
                .thenReturn(Optional.of(batch));

        when(processingBatchRepository.save(batch))
                .thenReturn(batch);

        CompleteProcessingBatchRequest request =
                new CompleteProcessingBatchRequest(
                        new BigDecimal("4.000"),
                        LocalDate.of(2026, 10, 25)
                );

        ProcessingBatchResponse response =
                processingBatchService.complete(
                        batchId,
                        request
                );

        assertThat(batch.getStatus())
                .isEqualTo(ProcessingStatus.COMPLETED);

        assertThat(batch.getOutputGreenBeanKg())
                .isEqualByComparingTo("4.000");

        assertThat(batch.getDryingEndDate())
                .isEqualTo(
                        LocalDate.of(2026, 10, 25)
                );

        verify(processingBatchRepository)
                .findById(batchId);

        verify(processingBatchRepository)
                .save(batch);
    }
}