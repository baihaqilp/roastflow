package com.flx.porto.roastflow.procurement.repository;

import com.flx.porto.roastflow.procurement.model.Farm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FarmRepository extends JpaRepository<Farm, UUID> {

    boolean existByNameIgnoreCase(String name);
}
