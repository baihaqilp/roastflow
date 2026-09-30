package com.flx.porto.roastflow.procurement.repository;

import com.flx.porto.roastflow.procurement.model.Supplier;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
class SupplierRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
              new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private SupplierRepository supplierRepository;


    @Test
    void shouldSaveAndFindSupplier() {

        Supplier supplier = new Supplier(
                "Pak Budi",
                "081234567890",
                "Banjarnegara, Jawa Tengah"
        );

        Supplier savedSupplier =
                supplierRepository.save(supplier);

        assertThat(savedSupplier.getId())
                .isNotNull();

        assertThat(savedSupplier.getName())
                .isEqualTo("Pak Budi");

        Optional<Supplier> result =
                supplierRepository.findById(savedSupplier.getId());

        assertThat(result)
                .isPresent();

        assertThat(result.get().getName())
                .isEqualTo("Pak Budi");

        assertThat(result.get().getPhone())
                .isEqualTo("081234567890");

        assertThat(result.get().getAddress())
                .isEqualTo("Banjarnegara, Jawa Tengah");
    }


    @Test
    void shouldFindSupplierByNameIgnoreCase() {

        Supplier supplier = new Supplier(
                "Pak Budi",
                "081234567890",
                "Banjarnegara"
        );

        supplierRepository.save(supplier);

        boolean exists =
                supplierRepository
                        .existsByNameIgnoreCase("PAK BUDI");

        assertThat(exists)
                .isTrue();
    }


    @Test
    void shouldReturnFalseWhenSupplierDoesNotExist() {

        boolean exists =
                supplierRepository
                        .existsByNameIgnoreCase("Supplier Tidak Ada");

        assertThat(exists)
                .isFalse();
    }
}