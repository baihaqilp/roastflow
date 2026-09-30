package com.flx.porto.roastflow.procurement.repository;

import com.flx.porto.roastflow.procurement.model.CoffeeVariety;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CoffeeVarietyRepository extends JpaRepository<CoffeeVariety, UUID> {

    boolean existsByNameIgnoreCase(String name);
}
