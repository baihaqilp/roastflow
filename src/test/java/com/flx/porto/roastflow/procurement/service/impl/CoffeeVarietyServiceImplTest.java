package com.flx.porto.roastflow.procurement.service.impl;

import com.flx.porto.roastflow.common.exception.DuplicateResourceException;
import com.flx.porto.roastflow.common.exception.ResourceNotFoundException;
import com.flx.porto.roastflow.procurement.model.CoffeeVariety;
import com.flx.porto.roastflow.procurement.model.dto.CoffeeVarietyResponse;
import com.flx.porto.roastflow.procurement.model.dto.CreateCoffeeVarietyRequest;
import com.flx.porto.roastflow.procurement.repository.CoffeeVarietyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoffeeVarietyServiceImplTest {

    @Mock
    private CoffeeVarietyRepository coffeeVarietyRepository;

    @InjectMocks
    private CoffeeVarietyServiceImpl coffeeVarietyService;

    @Test
    void shouldCreateCoffeeVarietySuccessfully() {

        CreateCoffeeVarietyRequest request =
                new CreateCoffeeVarietyRequest("Arabica");

        CoffeeVariety saved =
                new CoffeeVariety("Arabica");

        when(coffeeVarietyRepository.existsByNameIgnoreCase("Arabica"))
                .thenReturn(false);

        when(coffeeVarietyRepository.save(any(CoffeeVariety.class)))
                .thenReturn(saved);

        CoffeeVarietyResponse result =
                coffeeVarietyService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Arabica");

        verify(coffeeVarietyRepository)
                .existsByNameIgnoreCase("Arabica");

        verify(coffeeVarietyRepository)
                .save(any(CoffeeVariety.class));
    }

    @Test
    void shouldThrowExceptionWhenCoffeeVarietyAlreadyExists() {

        CreateCoffeeVarietyRequest request =
                new CreateCoffeeVarietyRequest("Arabica");

        when(coffeeVarietyRepository.existsByNameIgnoreCase("Arabica"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                coffeeVarietyService.create(request)
        )
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Coffee variety already exists: Arabica");

        verify(coffeeVarietyRepository, never())
                .save(any(CoffeeVariety.class));
    }

    @Test
    void shouldFindAllCoffeeVarieties() {

        CoffeeVariety arabica =
                new CoffeeVariety("Arabica");

        CoffeeVariety robusta =
                new CoffeeVariety("Robusta");

        when(coffeeVarietyRepository.findAll())
                .thenReturn(List.of(arabica, robusta));

        List<CoffeeVarietyResponse> result =
                coffeeVarietyService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Arabica");
        assertThat(result.get(1).name()).isEqualTo("Robusta");
    }

    @Test
    void shouldFindCoffeeVarietyById() {

        UUID id = UUID.randomUUID();

        CoffeeVariety coffeeVariety =
                new CoffeeVariety("Arabica");

        when(coffeeVarietyRepository.findById(id))
                .thenReturn(Optional.of(coffeeVariety));

        CoffeeVarietyResponse result =
                coffeeVarietyService.findById(id);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Arabica");
    }

    @Test
    void shouldThrowExceptionWhenCoffeeVarietyNotFound() {

        UUID id = UUID.randomUUID();

        when(coffeeVarietyRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                coffeeVarietyService.findById(id)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Coffee variety not found: " + id);
    }
}