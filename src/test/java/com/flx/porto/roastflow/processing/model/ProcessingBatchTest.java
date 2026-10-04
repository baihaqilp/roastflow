package com.flx.porto.roastflow.processing.model;

import com.flx.porto.roastflow.processing.common.ProcessingMethod;
import com.flx.porto.roastflow.processing.common.ProcessingStatus;
import com.flx.porto.roastflow.procurement.model.CherryLot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class ProcessingBatchTest {

    private CherryLot cherryLot;

    @BeforeEach
    void setUp() {
        cherryLot = mock(CherryLot.class);
    }

    private ProcessingBatch createBatch() {

        return new ProcessingBatch(
                cherryLot,
                "PB-2026-001",
                ProcessingMethod.NATURAL,
                new BigDecimal("20.000"),
                LocalDate.of(2026, 10, 1)
        );
    }

    @Test
    void shouldStartProcessingFromPlanned() {

        ProcessingBatch batch = createBatch();

        assertThat(batch.getStatus())
                .isEqualTo(ProcessingStatus.PLANNED);

        batch.startProcessing();

        assertThat(batch.getStatus())
                .isEqualTo(ProcessingStatus.PROCESSING);
    }

    @Test
    void shouldNotStartProcessingWhenAlreadyProcessing() {

        ProcessingBatch batch = createBatch();

        batch.startProcessing();

        assertThatThrownBy(batch::startProcessing)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(
                        "only start from PLANNED"
                );
    }

    @Test
    void shouldStartDryingFromProcessing() {

        ProcessingBatch batch = createBatch();

        batch.startProcessing();

        LocalDate dryingStartDate =
                LocalDate.of(2026, 10, 5);

        batch.startDrying(dryingStartDate);

        assertThat(batch.getStatus())
                .isEqualTo(ProcessingStatus.DRYING);

        assertThat(batch.getDryingStartDate())
                .isEqualTo(dryingStartDate);
    }

    @Test
    void shouldNotStartDryingFromPlanned() {

        ProcessingBatch batch = createBatch();

        assertThatThrownBy(() ->
                batch.startDrying(
                        LocalDate.of(2026, 10, 5)
                )
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(
                        "only start when processing batch is PROCESSING"
                );
    }

    @Test
    void shouldCompleteProcessingSuccessfully() {

        ProcessingBatch batch = createBatch();

        batch.startProcessing();

        batch.startDrying(
                LocalDate.of(2026, 10, 5)
        );

        batch.complete(
                LocalDate.of(2026, 10, 25),
                new BigDecimal("4.000")
        );

        assertThat(batch.getStatus())
                .isEqualTo(ProcessingStatus.COMPLETED);

        assertThat(batch.getDryingEndDate())
                .isEqualTo(
                        LocalDate.of(2026, 10, 25)
                );

        assertThat(batch.getOutputGreenBeanKg())
                .isEqualByComparingTo("4.000");
    }

    @Test
    void shouldNotCompleteFromPlanned() {

        ProcessingBatch batch = createBatch();

        assertThatThrownBy(() ->
                batch.complete(
                        LocalDate.of(2026, 10, 25),
                        new BigDecimal("4.000")
                )
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(
                        "only be completed from DRYING"
                );
    }

    @Test
    void shouldNotCompleteFromProcessing() {

        ProcessingBatch batch = createBatch();

        batch.startProcessing();

        assertThatThrownBy(() ->
                batch.complete(
                        LocalDate.of(2026, 10, 25),
                        new BigDecimal("4.000")
                )
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(
                        "only be completed from DRYING"
                );
    }

    @Test
    void shouldRejectDryingEndDateBeforeDryingStartDate() {

        ProcessingBatch batch = createBatch();

        batch.startProcessing();

        batch.startDrying(
                LocalDate.of(2026, 10, 25)
        );

        assertThatThrownBy(() ->
                batch.complete(
                        LocalDate.of(2026, 10, 20),
                        new BigDecimal("4.000")
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(
                        "Drying end date cannot be before"
                );
    }

    @Test
    void shouldRejectZeroOutputGreenBean() {

        ProcessingBatch batch = createBatch();

        batch.startProcessing();

        batch.startDrying(
                LocalDate.of(2026, 10, 5)
        );

        assertThatThrownBy(() ->
                batch.complete(
                        LocalDate.of(2026, 10, 25),
                        BigDecimal.ZERO
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(
                        "Output green bean weight must be greater than zero"
                );
    }

    @Test
    void shouldRejectOutputGreaterThanInput() {

        ProcessingBatch batch = createBatch();

        batch.startProcessing();

        batch.startDrying(
                LocalDate.of(2026, 10, 5)
        );

        assertThatThrownBy(() ->
                batch.complete(
                        LocalDate.of(2026, 10, 25),
                        new BigDecimal("21.000")
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(
                        "Output green bean weight cannot exceed input weight"
                );
    }
}