package com.flx.porto.roastflow.procurement.service.impl;

import com.flx.porto.roastflow.common.exception.DuplicateResourceException;
import com.flx.porto.roastflow.common.exception.ResourceNotFoundException;
import com.flx.porto.roastflow.procurement.model.Farm;
import com.flx.porto.roastflow.procurement.model.dto.CreateFarmRequest;
import com.flx.porto.roastflow.procurement.model.dto.FarmResponse;
import com.flx.porto.roastflow.procurement.repository.FarmRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FarmServiceImplTest {

    @Mock
    private FarmRepository farmRepository;

    @InjectMocks
    private FarmServiceImpl farmService;


    @Test
    void shouldCreateFarmSuccessfully() {

        CreateFarmRequest request = new CreateFarmRequest(
                "Kebun Pak Budi",
                "Banjarnegara, Jawa Tengah"
        );

        Farm farm = new Farm(
                request.name(),
                request.origin()
        );

        when(farmRepository.existsByNameIgnoreCase(request.name()))
                .thenReturn(false);

        when(farmRepository.save(any(Farm.class)))
                .thenReturn(farm);

        FarmResponse response = farmService.create(request);

        assertNotNull(response);
        assertEquals("Kebun Pak Budi", response.name());
        assertEquals(
                "Banjarnegara, Jawa Tengah",
                response.origin()
        );

        verify(farmRepository)
                .existsByNameIgnoreCase(request.name());

        verify(farmRepository)
                .save(any(Farm.class));
    }


    @Test
    void shouldThrowExceptionWhenFarmAlreadyExists() {

        CreateFarmRequest request = new CreateFarmRequest(
                "Kebun Pak Budi",
                "Banjarnegara, Jawa Tengah"
        );

        when(farmRepository.existsByNameIgnoreCase(request.name()))
                .thenReturn(true);

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> farmService.create(request)
                );

        assertEquals(
                "Farm already exists: Kebun Pak Budi",
                exception.getMessage()
        );

        verify(farmRepository)
                .existsByNameIgnoreCase(request.name());

        verify(farmRepository, never())
                .save(any(Farm.class));
    }


    @Test
    void shouldFindAllFarms() {

        Farm farm1 = new Farm(
                "Kebun Pak Budi",
                "Banjarnegara, Jawa Tengah"
        );

        Farm farm2 = new Farm(
                "Kebun Pak Joko",
                "Wonosobo, Jawa Tengah"
        );

        when(farmRepository.findAll())
                .thenReturn(List.of(farm1, farm2));

        List<FarmResponse> response =
                farmService.findAll();

        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals(
                "Kebun Pak Budi",
                response.get(0).name()
        );

        assertEquals(
                "Kebun Pak Joko",
                response.get(1).name()
        );

        assertEquals(
                "Banjarnegara, Jawa Tengah",
                response.get(0).origin()
        );

        assertEquals(
                "Wonosobo, Jawa Tengah",
                response.get(1).origin()
        );

        verify(farmRepository)
                .findAll();
    }


    @Test
    void shouldFindFarmById() {

        UUID farmId = UUID.randomUUID();

        Farm farm = new Farm(
                "Kebun Pak Budi",
                "Banjarnegara, Jawa Tengah"
        );

        when(farmRepository.findById(farmId))
                .thenReturn(Optional.of(farm));

        FarmResponse response =
                farmService.findById(farmId);

        assertNotNull(response);

        assertEquals(
                "Kebun Pak Budi",
                response.name()
        );

        assertEquals(
                "Banjarnegara, Jawa Tengah",
                response.origin()
        );

        verify(farmRepository)
                .findById(farmId);
    }


    @Test
    void shouldThrowExceptionWhenFarmNotFound() {

        UUID farmId = UUID.randomUUID();

        when(farmRepository.findById(farmId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> farmService.findById(farmId)
                );

        assertEquals(
                "Farm not found: " + farmId,
                exception.getMessage()
        );

        verify(farmRepository)
                .findById(farmId);
    }
}