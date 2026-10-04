package com.flx.porto.roastflow.processing.repository;

import com.flx.porto.roastflow.processing.common.ProcessingMethod;
import com.flx.porto.roastflow.processing.model.ProcessingBatch;
import com.flx.porto.roastflow.procurement.model.CherryLot;
import com.flx.porto.roastflow.procurement.model.Farm;
import com.flx.porto.roastflow.procurement.model.Purchase;
import com.flx.porto.roastflow.procurement.model.Supplier;
import com.flx.porto.roastflow.procurement.model.CoffeeVariety;
import com.flx.porto.roastflow.procurement.repository.CherryLotRepository;
import com.flx.porto.roastflow.procurement.repository.FarmRepository;
import com.flx.porto.roastflow.procurement.repository.PurchaseRepository;
import com.flx.porto.roastflow.procurement.repository.SupplierRepository;
import com.flx.porto.roastflow.procurement.repository.CoffeeVarietyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
class ProcessingBatchRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:18");

    @DynamicPropertySource
    static void configureProperties(
            DynamicPropertyRegistry registry
    ) {
        registry.add(
                "spring.datasource.url",
                postgres::getJdbcUrl
        );
        registry.add(
                "spring.datasource.username",
                postgres::getUsername
        );
        registry.add(
                "spring.datasource.password",
                postgres::getPassword
        );
    }

    @Autowired
    private ProcessingBatchRepository processingBatchRepository;

    @Autowired
    private CherryLotRepository cherryLotRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private CoffeeVarietyRepository coffeeVarietyRepository;

    private CherryLot cherryLot;

    @BeforeEach
    void setUp() {

        Supplier supplier = supplierRepository.save(
                new Supplier(
                        "Pak Budi",
                        "081234567890",
                        "Banjarnegara, Jawa Tengah"
                )
        );

        Farm farm = farmRepository.save(
                new Farm(
                        "Kebun Budi",
                        "Banjarnegara Jawa Tengah"
                )
        );

        CoffeeVariety variety = coffeeVarietyRepository.save(
                new CoffeeVariety(
                        "Arabica"
                )
        );

        Purchase purchase = purchaseRepository.save(
                new Purchase(
                        supplier,
                        farm,
                        variety,
                        LocalDate.of(2026, 10, 1),
                        new BigDecimal("50000.00"),
                        new BigDecimal("50.000")
                )
        );

        cherryLot = cherryLotRepository.save(
                new CherryLot(
                        purchase,
                        "CL-2026-001",
                        new BigDecimal("50.000")
                )
        );
    }

    @Test
    void shouldSaveAndFindProcessingBatch() {

        ProcessingBatch batch = new ProcessingBatch(
                cherryLot,
                "PB-2026-001",
                ProcessingMethod.NATURAL,
                new BigDecimal("20.000"),
                LocalDate.of(2026, 10, 1)
        );

        ProcessingBatch saved =
                processingBatchRepository.save(batch);

        assertThat(saved.getId()).isNotNull();

        assertThat(saved.getBatchCode())
                .isEqualTo("PB-2026-001");

        assertThat(saved.getProcessingMethod())
                .isEqualTo(ProcessingMethod.NATURAL);

        assertThat(saved.getInputWeightKg())
                .isEqualByComparingTo("20.000");

        assertThat(saved.getCherryLot().getId())
                .isEqualTo(cherryLot.getId());
    }

    @Test
    void shouldFindByBatchCodeIgnoringCase() {

        ProcessingBatch batch = new ProcessingBatch(
                cherryLot,
                "PB-2026-002",
                ProcessingMethod.HONEY,
                new BigDecimal("15.000"),
                LocalDate.of(2026, 10, 1)
        );

        processingBatchRepository.save(batch);

        assertThat(
                processingBatchRepository
                        .existsByBatchCodeIgnoreCase("pb-2026-002")
        )
                .isTrue();
    }

    @Test
    void shouldReturnFalseWhenBatchCodeDoesNotExist() {

        assertThat(
                processingBatchRepository
                        .existsByBatchCodeIgnoreCase("PB-NOT-EXIST")
        )
                .isFalse();
    }

    @Test
    void shouldDetectDuplicateBatchCodeIgnoringCase() {

        ProcessingBatch firstBatch = new ProcessingBatch(
                cherryLot,
                "PB-2026-003",
                ProcessingMethod.NATURAL,
                new BigDecimal("10.000"),
                LocalDate.of(2026, 10, 1)
        );

        processingBatchRepository.saveAndFlush(firstBatch);

        assertThat(
                processingBatchRepository
                        .existsByBatchCodeIgnoreCase("pb-2026-003")
        )
                .isTrue();
    }
}