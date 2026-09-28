package com.flx.porto.roastflow.procurement.repository;

import com.flx.porto.roastflow.procurement.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {

    boolean existsByNameIgnoreCase(String name);
}
