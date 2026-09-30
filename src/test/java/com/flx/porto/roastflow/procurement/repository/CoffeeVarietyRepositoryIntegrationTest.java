package com.flx.porto.roastflow.procurement.repository;

import com.flx.porto.roastflow.procurement.model.CoffeeVariety;
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
class CoffeeVarietyRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:18");

    @Autowired
    private CoffeeVarietyRepository coffeeVarietyRepository;

    @Test
    void shouldSaveAndFindCoffeeVariety() {

        CoffeeVariety coffeeVariety =
                new CoffeeVariety("Arabica");

        CoffeeVariety saved =
                coffeeVarietyRepository.save(coffeeVariety);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Arabica");

        Optional<CoffeeVariety> result =
                coffeeVarietyRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName())
                .isEqualTo("Arabica");
    }

    @Test
    void shouldFindCoffeeVarietyByNameIgnoreCase() {

        CoffeeVariety coffeeVariety =
                new CoffeeVariety("Arabica");

        coffeeVarietyRepository.save(coffeeVariety);

        boolean exists =
                coffeeVarietyRepository
                        .existsByNameIgnoreCase("ARABICA");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenCoffeeVarietyDoesNotExist() {

        boolean exists =
                coffeeVarietyRepository
                        .existsByNameIgnoreCase("Liberica");

        assertThat(exists).isFalse();
    }
}