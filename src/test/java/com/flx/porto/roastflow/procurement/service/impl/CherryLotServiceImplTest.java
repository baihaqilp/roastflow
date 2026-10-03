package com.flx.porto.roastflow.procurement.service.impl;

import com.flx.porto.roastflow.common.exception.DuplicateResourceException;
import com.flx.porto.roastflow.common.exception.ResourceNotFoundException;
import com.flx.porto.roastflow.procurement.model.CherryLot;
import com.flx.porto.roastflow.procurement.model.Farm;
import com.flx.porto.roastflow.procurement.model.Purchase;
import com.flx.porto.roastflow.procurement.model.Supplier;
import com.flx.porto.roastflow.procurement.model.CoffeeVariety;
import com.flx.porto.roastflow.procurement.model.dto.CherryLotResponse;
import com.flx.porto.roastflow.procurement.model.dto.CreateCherryLotRequest;
import com.flx.porto.roastflow.procurement.repository.CherryLotRepository;
import com.flx.porto.roastflow.procurement.repository.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CherryLotServiceImplTest {

    @Mock
    private CherryLotRepository cherryLotRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @InjectMocks
    private CherryLotServiceImpl cherryLotService;

    private Purchase purchase;

    @BeforeEach
    void setUp() {
        Supplier supplier = new Supplier(
                "Pak Budi",
                "081234567890",
                "Banjarnegara, Jawa Tengah"
        );

        Farm farm = new Farm(
                "Kebun Budi",
                "Banjarnegara"
        );

        CoffeeVariety variety = new CoffeeVariety("Arabica");

        purchase = new Purchase(
                supplier,
                farm,
                variety,
                LocalDate.of(2026, 10, 1),
                new BigDecimal("35000"),
                new BigDecimal("50.000")
        );
    }

    @Test
    void shouldCreateCherryLotSuccessfully() {

        UUID purchaseId = UUID.randomUUID();

        CreateCherryLotRequest request =
                new CreateCherryLotRequest(
                        purchaseId,
                        "CH-2026-001",
                        new BigDecimal("20.000")
                );

        when(cherryLotRepository.existsByLotCodeIgnoreCase("CH-2026-001"))
                .thenReturn(false);

        when(purchaseRepository.findById(purchaseId))
                .thenReturn(java.util.Optional.of(purchase));

        when(cherryLotRepository.save(any(CherryLot.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CherryLotResponse response =
                cherryLotService.create(request);

        assertThat(response.lotCode())
                .isEqualTo("CH-2026-001");

        assertThat(response.initialWeightKg())
                .isEqualByComparingTo("20.000");

        assertThat(response.remainingWeightKg())
                .isEqualByComparingTo("20.000");

        assertThat(response.status())
                .isEqualTo("AVAILABLE");

        verify(cherryLotRepository).save(any(CherryLot.class));
    }

    @Test
    void shouldRejectDuplicateLotCode() {

        UUID purchaseId = UUID.randomUUID();

        CreateCherryLotRequest request =
                new CreateCherryLotRequest(
                        purchaseId,
                        "CH-2026-001",
                        new BigDecimal("20.000")
                );

        when(cherryLotRepository.existsByLotCodeIgnoreCase("CH-2026-001"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                cherryLotService.create(request)
        )
                .isInstanceOf(DuplicateResourceException.class);

        verify(purchaseRepository, never()).findById(any());
        verify(cherryLotRepository, never()).save(any());
    }

    @Test
    void shouldRejectWhenPurchaseDoesNotExist() {

        UUID purchaseId = UUID.randomUUID();

        CreateCherryLotRequest request =
                new CreateCherryLotRequest(
                        purchaseId,
                        "CH-2026-001",
                        new BigDecimal("20.000")
                );

        when(cherryLotRepository.existsByLotCodeIgnoreCase("CH-2026-001"))
                .thenReturn(false);

        when(purchaseRepository.findById(purchaseId))
                .thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() ->
                cherryLotService.create(request)
        )
                .isInstanceOf(ResourceNotFoundException.class);

        verify(cherryLotRepository, never()).save(any());
    }

    @Test
    void shouldRejectWeightGreaterThanPurchaseWeight() {

        UUID purchaseId = UUID.randomUUID();

        CreateCherryLotRequest request =
                new CreateCherryLotRequest(
                        purchaseId,
                        "CH-2026-001",
                        new BigDecimal("60.000")
                );

        when(cherryLotRepository.existsByLotCodeIgnoreCase("CH-2026-001"))
                .thenReturn(false);

        when(purchaseRepository.findById(purchaseId))
                .thenReturn(java.util.Optional.of(purchase));

        assertThatThrownBy(() ->
                cherryLotService.create(request)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cherry lot weight cannot exceed purchase weight");

        verify(cherryLotRepository, never()).save(any());
    }

    @Test
    void shouldFindAllCherryLots() {

        UUID lotId = UUID.randomUUID();

        CherryLot lot = new CherryLot(
                purchase,
                "CH-2026-001",
                new BigDecimal("20.000")
        );

        when(cherryLotRepository.findAll())
                .thenReturn(List.of(lot));

        List<CherryLotResponse> result =
                cherryLotService.findAll();

        assertThat(result)
                .hasSize(1);

        assertThat(result.getFirst().lotCode())
                .isEqualTo("CH-2026-001");
    }

    @Test
    void shouldFindCherryLotById() {

        UUID lotId = UUID.randomUUID();

        CherryLot lot = new CherryLot(
                purchase,
                "CH-2026-001",
                new BigDecimal("20.000")
        );

        when(cherryLotRepository.findById(lotId))
                .thenReturn(java.util.Optional.of(lot));

        CherryLotResponse response =
                cherryLotService.findById(lotId);

        assertThat(response.lotCode())
                .isEqualTo("CH-2026-001");
    }

    @Test
    void shouldThrowWhenCherryLotDoesNotExist() {

        UUID lotId = UUID.randomUUID();

        when(cherryLotRepository.findById(lotId))
                .thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() ->
                cherryLotService.findById(lotId)
        )
                .isInstanceOf(ResourceNotFoundException.class);
    }
}