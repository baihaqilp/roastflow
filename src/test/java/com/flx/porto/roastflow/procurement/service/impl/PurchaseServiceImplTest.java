package com.flx.porto.roastflow.procurement.service.impl;

import com.flx.porto.roastflow.common.exception.ResourceNotFoundException;
import com.flx.porto.roastflow.procurement.model.CoffeeVariety;
import com.flx.porto.roastflow.procurement.model.Farm;
import com.flx.porto.roastflow.procurement.model.Purchase;
import com.flx.porto.roastflow.procurement.model.Supplier;
import com.flx.porto.roastflow.procurement.model.dto.CreatePurchaseRequest;
import com.flx.porto.roastflow.procurement.model.dto.PurchaseResponse;
import com.flx.porto.roastflow.procurement.repository.CoffeeVarietyRepository;
import com.flx.porto.roastflow.procurement.repository.FarmRepository;
import com.flx.porto.roastflow.procurement.repository.PurchaseRepository;
import com.flx.porto.roastflow.procurement.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceImplTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private FarmRepository farmRepository;

    @Mock
    private CoffeeVarietyRepository coffeeVarietyRepository;

    private PurchaseServiceImpl purchaseService;

    private Supplier supplier;
    private Farm farm;
    private CoffeeVariety coffeeVariety;

    @BeforeEach
    void setUp() {
        purchaseService = new PurchaseServiceImpl(
                purchaseRepository,
                supplierRepository,
                farmRepository,
                coffeeVarietyRepository
        );

        supplier = new Supplier(
                "Pak Budi",
                "081234567890",
                "Banjarnegara"
        );

        farm = new Farm(
                "Kebun Pak Budi",
                "Banjarnegara, Jawa Tengah"
        );

        coffeeVariety = new CoffeeVariety("Sigarar Utang");
    }

    @Test
    void shouldCreatePurchaseSuccessfully() {

        UUID supplierId = UUID.randomUUID();
        UUID farmId = UUID.randomUUID();
        UUID varietyId = UUID.randomUUID();

        CreatePurchaseRequest request = new CreatePurchaseRequest(
                supplierId,
                farmId,
                varietyId,
                LocalDate.of(2026, 9, 30),
                new BigDecimal("35000"),
                new BigDecimal("10.500")
        );

        when(supplierRepository.findById(supplierId))
                .thenReturn(Optional.of(supplier));

        when(farmRepository.findById(farmId))
                .thenReturn(Optional.of(farm));

        when(coffeeVarietyRepository.findById(varietyId))
                .thenReturn(Optional.of(coffeeVariety));

        Purchase savedPurchase = new Purchase(
                supplier,
                farm,
                coffeeVariety,
                request.purchaseDate(),
                request.pricePerKg(),
                request.totalWeightKg()
        );

        when(purchaseRepository.save(any(Purchase.class)))
                .thenReturn(savedPurchase);

        PurchaseResponse response = purchaseService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.supplierName()).isEqualTo("Pak Budi");
        assertThat(response.farmName()).isEqualTo("Kebun Pak Budi");
        assertThat(response.varietyName()).isEqualTo("Sigarar Utang");
        assertThat(response.pricePerKg())
                .isEqualByComparingTo("35000");
        assertThat(response.totalWeightKg())
                .isEqualByComparingTo("10.500");
        assertThat(response.totalAmount())
                .isEqualByComparingTo("367500");

        verify(purchaseRepository).save(any(Purchase.class));
    }

    @Test
    void shouldCalculateTotalAmountCorrectly() {

        UUID supplierId = UUID.randomUUID();
        UUID farmId = UUID.randomUUID();
        UUID varietyId = UUID.randomUUID();

        CreatePurchaseRequest request = new CreatePurchaseRequest(
                supplierId,
                farmId,
                varietyId,
                LocalDate.of(2026, 9, 30),
                new BigDecimal("42000"),
                new BigDecimal("12.500")
        );

        when(supplierRepository.findById(supplierId))
                .thenReturn(Optional.of(supplier));

        when(farmRepository.findById(farmId))
                .thenReturn(Optional.of(farm));

        when(coffeeVarietyRepository.findById(varietyId))
                .thenReturn(Optional.of(coffeeVariety));

        when(purchaseRepository.save(any(Purchase.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PurchaseResponse response = purchaseService.create(request);

        assertThat(response.totalAmount())
                .isEqualByComparingTo("525000");
    }

    @Test
    void shouldThrowExceptionWhenSupplierNotFound() {

        UUID supplierId = UUID.randomUUID();
        UUID farmId = UUID.randomUUID();
        UUID varietyId = UUID.randomUUID();

        CreatePurchaseRequest request = new CreatePurchaseRequest(
                supplierId,
                farmId,
                varietyId,
                LocalDate.of(2026, 9, 30),
                new BigDecimal("35000"),
                new BigDecimal("10.000")
        );

        when(supplierRepository.findById(supplierId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> purchaseService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Supplier not found");

        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenFarmNotFound() {

        UUID supplierId = UUID.randomUUID();
        UUID farmId = UUID.randomUUID();
        UUID varietyId = UUID.randomUUID();

        CreatePurchaseRequest request = new CreatePurchaseRequest(
                supplierId,
                farmId,
                varietyId,
                LocalDate.of(2026, 9, 30),
                new BigDecimal("35000"),
                new BigDecimal("10.000")
        );

        when(supplierRepository.findById(supplierId))
                .thenReturn(Optional.of(supplier));

        when(farmRepository.findById(farmId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> purchaseService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Farm not found");

        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCoffeeVarietyNotFound() {

        UUID supplierId = UUID.randomUUID();
        UUID farmId = UUID.randomUUID();
        UUID varietyId = UUID.randomUUID();

        CreatePurchaseRequest request = new CreatePurchaseRequest(
                supplierId,
                farmId,
                varietyId,
                LocalDate.of(2026, 9, 30),
                new BigDecimal("35000"),
                new BigDecimal("10.000")
        );

        when(supplierRepository.findById(supplierId))
                .thenReturn(Optional.of(supplier));

        when(farmRepository.findById(farmId))
                .thenReturn(Optional.of(farm));

        when(coffeeVarietyRepository.findById(varietyId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> purchaseService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Coffee variety not found");

        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void shouldFindAllPurchases() {

        Purchase purchase1 = new Purchase(
                supplier,
                farm,
                coffeeVariety,
                LocalDate.of(2026, 9, 29),
                new BigDecimal("35000"),
                new BigDecimal("10.000")
        );

        Purchase purchase2 = new Purchase(
                supplier,
                farm,
                coffeeVariety,
                LocalDate.of(2026, 9, 30),
                new BigDecimal("40000"),
                new BigDecimal("15.000")
        );

        when(purchaseRepository.findAll())
                .thenReturn(List.of(purchase1, purchase2));

        List<PurchaseResponse> result = purchaseService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).pricePerKg())
                .isEqualByComparingTo("35000");
        assertThat(result.get(1).pricePerKg())
                .isEqualByComparingTo("40000");

        verify(purchaseRepository).findAll();
    }

    @Test
    void shouldFindPurchaseById() {

        UUID purchaseId = UUID.randomUUID();

        Purchase purchase = new Purchase(
                supplier,
                farm,
                coffeeVariety,
                LocalDate.of(2026, 9, 30),
                new BigDecimal("35000"),
                new BigDecimal("10.000")
        );

        when(purchaseRepository.findById(purchaseId))
                .thenReturn(Optional.of(purchase));

        PurchaseResponse response =
                purchaseService.findById(purchaseId);

        assertThat(response).isNotNull();
        assertThat(response.supplierName()).isEqualTo("Pak Budi");
        assertThat(response.farmName()).isEqualTo("Kebun Pak Budi");
        assertThat(response.varietyName()).isEqualTo("Sigarar Utang");

        verify(purchaseRepository).findById(purchaseId);
    }

    @Test
    void shouldThrowExceptionWhenPurchaseNotFound() {

        UUID purchaseId = UUID.randomUUID();

        when(purchaseRepository.findById(purchaseId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> purchaseService.findById(purchaseId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Purchase not found");

        verify(purchaseRepository).findById(purchaseId);
    }
}