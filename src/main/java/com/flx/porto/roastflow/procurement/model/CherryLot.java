package com.flx.porto.roastflow.procurement.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "cherry_lots",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_cherry_lots_lot_code", columnNames = "lot_code")
        }
)
public class CherryLot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase;

    @Column(name = "lot_code", nullable = false, length = 50)
    private String lotCode;

    @Column(
            name = "initial_weight_kg",
            nullable = false,
            precision = 15,
            scale = 3
    )
    private BigDecimal initialWeightKg;

    @Column(
            name = "remaining_weight_kg",
            nullable = false,
            precision = 15,
            scale = 3
    )
    private BigDecimal remainingWeightKg;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected CherryLot() {
    }

    public CherryLot(
            Purchase purchase,
            String lotCode,
            BigDecimal initialWeightKg
    ) {
        this.purchase = purchase;
        this.lotCode = lotCode;
        this.initialWeightKg = initialWeightKg;
        this.remainingWeightKg = initialWeightKg;
        this.status = "AVAILABLE";
    }

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public String getLotCode() {
        return lotCode;
    }

    public BigDecimal getInitialWeightKg() {
        return initialWeightKg;
    }

    public BigDecimal getRemainingWeightKg() {
        return remainingWeightKg;
    }

    public String getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}