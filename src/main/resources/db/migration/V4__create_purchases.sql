CREATE TABLE purchases(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    supplier_id UUID NOT NULL,
    farms_id UUID NOT NULL,
    variety_id UUID NOT NULL,

    purchase_date DATE NOT NULL,

    price_per_kg NUMERIC(15,2) NOT NULL,
    total_weight_kg NUMERIC(15,3) NOT NULL,
    total_amount NUMERIC(18,2) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_purchases_supplier
        FOREIGN KEY (supplier_id)
        REFERENCES suppliers(id),

    CONSTRAINT fk_purchases_farm
        FOREIGN KEY (farms_id)
        REFERENCES farms(id),

    CONSTRAINT fk_purchases_variety
        FOREIGN KEY (variety_id)
        REFERENCES coffee_varieties(id),

    CONSTRAINT chk_purchases_price_positive
        CHECK (price_per_kg > 0),

    CONSTRAINT chk_purchases_weight_positive
        CHECK (total_weight_kg > 0),

    CONSTRAINT chk_purchases_amount_positive
        CHECK (total_amount > 0)
);


CREATE INDEX idx_purchases_supplier_id
    ON purchases(supplier_id);

CREATE INDEX idx_purchases_farms_id
    ON purchases(farms_id);

CREATE INDEX idx_purchases_variety_id
    ON purchases(variety_id);

CREATE INDEX idx_purchases_purchase_date
    ON purchases(purchase_date);