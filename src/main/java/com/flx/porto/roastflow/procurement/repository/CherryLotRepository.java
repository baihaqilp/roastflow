package com.flx.porto.roastflow.procurement.repository;

import com.flx.porto.roastflow.procurement.model.CherryLot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CherryLotRepository extends JpaRepository<CherryLot, UUID> {

    boolean existsByLotCodeIgnoreCase(String lotCode);
}