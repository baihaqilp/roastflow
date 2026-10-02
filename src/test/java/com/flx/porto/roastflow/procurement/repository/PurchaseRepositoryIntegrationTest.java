package com.flx.porto.roastflow.procurement.repository;

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
class PurchaseRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:18");

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private CoffeeVarietyRepository coffeeVarietyRepository;

    @Test
    void shouldSaveAndFindPurchase() {

        Supplier supplier = supplierRepository.save(
                new Supplier(
                        "Pak Budi",
                        "081234567890",
                        "Banjarnegara"
                )
        );

        Farm farm = farmRepository.save(
                new Farm(
                        "Kebun Pak Budi",
                        "Banjarnegara, Jawa Tengah"
                )
        );

        CoffeeVariety variety = coffeeVarietyRepository.save(
                new CoffeeVariety("Sigarar Utang")
        );

        Purchase purchase = new Purchase(
                supplier,
                farm,
                variety,
                LocalDate.of(2026, 9, 30),
                new BigDecimal("35000"),
                new BigDecimal("10.500")
        );

        Purchase savedPurchase = purchaseRepository.save(purchase);

        assertThat(savedPurchase.getId()).isNotNull();
        assertThat(savedPurchase.getSupplier().getName())
                .isEqualTo("Pak Budi");
        assertThat(savedPurchase.getFarm().getName())
                .isEqualTo("Kebun Pak Budi");
        assertThat(savedPurchase.getVariety().getName())
                .isEqualTo("Sigarar Utang");
        assertThat(savedPurchase.getPurchaseDate())
                .isEqualTo(LocalDate.of(2026, 9, 30));
        assertThat(savedPurchase.getPricePerKg())
                .isEqualByComparingTo("35000");
        assertThat(savedPurchase.getTotalWeightKg())
                .isEqualByComparingTo("10.500");
        assertThat(savedPurchase.getTotalAmount())
                .isEqualByComparingTo("367500");

        Purchase foundPurchase = purchaseRepository
                .findById(savedPurchase.getId())
                .orElseThrow();

        assertThat(foundPurchase.getId())
                .isEqualTo(savedPurchase.getId());
        assertThat(foundPurchase.getSupplier().getId())
                .isEqualTo(supplier.getId());
        assertThat(foundPurchase.getFarm().getId())
                .isEqualTo(farm.getId());
        assertThat(foundPurchase.getVariety().getId())
                .isEqualTo(variety.getId());
    }

    @Test
    void shouldPersistCalculatedTotalAmount() {

        Supplier supplier = supplierRepository.save(
                new Supplier("Pak Budi", null, "Banjarnegara")
        );

        Farm farm = farmRepository.save(
                new Farm("Kebun Budi", "Banjarnegara")
        );

        CoffeeVariety variety = coffeeVarietyRepository.save(
                new CoffeeVariety("Andungsari")
        );

        Purchase purchase = new Purchase(
                supplier,
                farm,
                variety,
                LocalDate.of(2026, 10, 1),
                new BigDecimal("42000"),
                new BigDecimal("12.500")
        );

        Purchase savedPurchase = purchaseRepository.save(purchase);

        assertThat(savedPurchase.getTotalAmount())
                .isEqualByComparingTo("525000");
    }

    @Test
    void shouldFindAllPurchases() {

        Supplier supplier = supplierRepository.save(
                new Supplier("Pak Budi", null, "Banjarnegara")
        );

        Farm farm = farmRepository.save(
                new Farm("Kebun Budi", "Banjarnegara")
        );

        CoffeeVariety variety = coffeeVarietyRepository.save(
                new CoffeeVariety("Sigarar Utang")
        );

        purchaseRepository.save(
                new Purchase(
                        supplier,
                        farm,
                        variety,
                        LocalDate.of(2026, 9, 29),
                        new BigDecimal("35000"),
                        new BigDecimal("10.000")
                )
        );

        purchaseRepository.save(
                new Purchase(
                        supplier,
                        farm,
                        variety,
                        LocalDate.of(2026, 9, 30),
                        new BigDecimal("40000"),
                        new BigDecimal("15.000")
                )
        );

        var purchases = purchaseRepository.findAll();

        assertThat(purchases).hasSize(2);
        assertThat(purchases)
                .extracting(Purchase::getTotalAmount)
                .usingElementComparator(BigDecimal::compareTo)
                .containsExactlyInAnyOrder(
                        new BigDecimal("350000"),
                        new BigDecimal("600000")
                );
    }
}