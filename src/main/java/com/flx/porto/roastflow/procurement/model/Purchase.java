package com.flx.porto.roastflow.procurement.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "purchases")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variety_id", nullable = false)
    private CoffeeVariety variety;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Column(name = "price_per_kg", nullable = false, precision = 15, scale = 2)
    private BigDecimal pricePerKg;

    @Column(name = "total_weight_kg", nullable = false, precision = 15, scale = 3)
    private BigDecimal totalWeightKg;

    @Column(name = "total_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected Purchase() {
    }

    public Purchase(
            Supplier supplier,
            Farm farm,
            CoffeeVariety variety,
            LocalDate purchaseDate,
            BigDecimal pricePerKg,
            BigDecimal totalWeightKg
    ) {
        this.supplier = supplier;
        this.farm = farm;
        this.variety = variety;
        this.purchaseDate = purchaseDate;
        this.pricePerKg = pricePerKg;
        this.totalWeightKg = totalWeightKg;
        this.totalAmount = pricePerKg.multiply(totalWeightKg);
    }

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public Farm getFarm() {
        return farm;
    }

    public CoffeeVariety getVariety() {
        return variety;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public BigDecimal getPricePerKg() {
        return pricePerKg;
    }

    public BigDecimal getTotalWeightKg() {
        return totalWeightKg;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}