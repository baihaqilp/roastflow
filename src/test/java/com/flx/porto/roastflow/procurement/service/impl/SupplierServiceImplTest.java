package com.flx.porto.roastflow.procurement.service.impl;

import com.flx.porto.roastflow.common.exception.DuplicateResourceException;
import com.flx.porto.roastflow.common.exception.ResourceNotFoundException;
import com.flx.porto.roastflow.procurement.model.Supplier;
import com.flx.porto.roastflow.procurement.model.dto.CreateSupplierRequest;
import com.flx.porto.roastflow.procurement.model.dto.SupplierResponse;
import com.flx.porto.roastflow.procurement.repository.SupplierRepository;

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
class SupplierServiceImplTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierServiceImpl supplierService;


    @Test
    void shouldCreateSupplierSuccessfully() {

        CreateSupplierRequest request = new CreateSupplierRequest(
                "Pak Budi",
                "081234567890",
                "Banjarnegara, Jawa Tengah"
        );

        Supplier supplier = new Supplier(
                request.name(),
                request.phone(),
                request.address()
        );

        when(supplierRepository.existsByNameIgnoreCase(request.name()))
                .thenReturn(false);

        when(supplierRepository.save(any(Supplier.class)))
                .thenReturn(supplier);

        SupplierResponse response = supplierService.create(request);

        assertNotNull(response);
        assertEquals("Pak Budi", response.name());
        assertEquals("081234567890", response.phone());
        assertEquals(
                "Banjarnegara, Jawa Tengah",
                response.address()
        );

        verify(supplierRepository)
                .existsByNameIgnoreCase(request.name());

        verify(supplierRepository)
                .save(any(Supplier.class));
    }


    @Test
    void shouldThrowExceptionWhenSupplierAlreadyExists() {

        CreateSupplierRequest request = new CreateSupplierRequest(
                "Pak Budi",
                "081234567890",
                "Banjarnegara, Jawa Tengah"
        );

        when(supplierRepository.existsByNameIgnoreCase(request.name()))
                .thenReturn(true);

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> supplierService.create(request)
                );

        assertEquals(
                "Supplier already exists: Pak Budi",
                exception.getMessage()
        );

        verify(supplierRepository)
                .existsByNameIgnoreCase(request.name());

        verify(supplierRepository, never())
                .save(any(Supplier.class));
    }


    @Test
    void shouldFindAllSuppliers() {

        Supplier supplier1 = new Supplier(
                "Pak Budi",
                "081234567890",
                "Banjarnegara"
        );

        Supplier supplier2 = new Supplier(
                "Pak Joko",
                "082234567890",
                "Wonosobo"
        );

        when(supplierRepository.findAll())
                .thenReturn(List.of(supplier1, supplier2));

        List<SupplierResponse> response =
                supplierService.findAll();

        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals("Pak Budi", response.get(0).name());
        assertEquals("Pak Joko", response.get(1).name());

        verify(supplierRepository)
                .findAll();
    }


    @Test
    void shouldFindSupplierById() {

        UUID supplierId = UUID.randomUUID();

        Supplier supplier = new Supplier(
                "Pak Budi",
                "081234567890",
                "Banjarnegara"
        );

        when(supplierRepository.findById(supplierId))
                .thenReturn(Optional.of(supplier));

        SupplierResponse response =
                supplierService.findById(supplierId);

        assertNotNull(response);
        assertEquals("Pak Budi", response.name());
        assertEquals("081234567890", response.phone());
        assertEquals("Banjarnegara", response.address());

        verify(supplierRepository)
                .findById(supplierId);
    }


    @Test
    void shouldThrowExceptionWhenSupplierNotFound() {

        UUID supplierId = UUID.randomUUID();

        when(supplierRepository.findById(supplierId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> supplierService.findById(supplierId)
                );

        assertEquals(
                "Supplier not found: " + supplierId,
                exception.getMessage()
        );

        verify(supplierRepository)
                .findById(supplierId);
    }
}