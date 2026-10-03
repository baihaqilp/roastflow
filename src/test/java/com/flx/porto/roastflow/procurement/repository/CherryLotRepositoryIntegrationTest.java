package com.flx.porto.roastflow.procurement.repository;

import com.flx.porto.roastflow.procurement.model.CherryLot;
import com.flx.porto.roastflow.procurement.model.CoffeeVariety;
import com.flx.porto.roastflow.procurement.model.Farm;
import com.flx.porto.roastflow.procurement.model.Purchase;
import com.flx.porto.roastflow.procurement.model.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
class CherryLotRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:18");

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

    @Test
    void shouldSaveAndFindCherryLot() {

        Supplier supplier =
                supplierRepository.save(
                        new Supplier(
                "Pak Budi",
                "081234567890",
                "Banjarnegara, Jawa Tengah"
        )
                );

        Farm farm =
                farmRepository.save(
                        new Farm(
                                "Kebun Budi",
                                "Banjarnegara"
                        )
                );

        CoffeeVariety variety =
                coffeeVarietyRepository.save(
                        new CoffeeVariety("Arabica")
                );

        Purchase purchase =
                purchaseRepository.save(
                        new Purchase(
                                supplier,
                                farm,
                                variety,
                                LocalDate.of(2026, 10, 1),
                                new BigDecimal("35000"),
                                new BigDecimal("50.000")
                        )
                );

        CherryLot lot =
                new CherryLot(
                        purchase,
                        "CH-2026-001",
                        new BigDecimal("20.000")
                );

        CherryLot saved =
                cherryLotRepository.save(lot);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getLotCode())
                .isEqualTo("CH-2026-001");

        assertThat(saved.getInitialWeightKg())
                .isEqualByComparingTo("20.000");

        assertThat(saved.getRemainingWeightKg())
                .isEqualByComparingTo("20.000");

        assertThat(saved.getStatus())
                .isEqualTo("AVAILABLE");

        assertThat(saved.getPurchase().getId())
                .isEqualTo(purchase.getId());
    }

    @Test
    void shouldDetectDuplicateLotCode() {

        Supplier supplier =
                supplierRepository.save(
                        new Supplier(
                "Pak Budi",
                "081234567890",
                "Banjarnegara, Jawa Tengah"
        )
                );

        Farm farm =
                farmRepository.save(
                        new Farm(
                                "Kebun Budi",
                                "Banjarnegara"
                        )
                );

        CoffeeVariety variety =
                coffeeVarietyRepository.save(
                        new CoffeeVariety("Arabica")
                );

        Purchase purchase =
                purchaseRepository.save(
                        new Purchase(
                                supplier,
                                farm,
                                variety,
                                LocalDate.of(2026, 10, 1),
                                new BigDecimal("35000"),
                                new BigDecimal("50.000")
                        )
                );

        cherryLotRepository.save(
                new CherryLot(
                        purchase,
                        "CH-2026-001",
                        new BigDecimal("20.000")
                )
        );

        assertThat(
                cherryLotRepository
                        .existsByLotCodeIgnoreCase("CH-2026-001")
        ).isTrue();
    }
}