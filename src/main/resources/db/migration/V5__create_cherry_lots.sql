CREATE TABLE cherry_lots(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    purchase_id UUID NOT NULL,

    lot_code VARCHAR(50) NOT NULL,

    initial_weight_kg NUMERIC(15,3) NOT NULL,
    remaining_weight_kg NUMERIC(15,3) NOT NULL,

    status VARCHAR(20) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_cherry_lots_lot_code
            UNIQUE (lot_code),

    CONSTRAINT fk_cherry_lots_purchase
        FOREIGN KEY (purchase_id)
        REFERENCES purchases(id),

    CONSTRAINT chk_cherry_lots_initial_weight
        CHECK (initial_weight_kg > 0),

    CONSTRAINT chk_cherry_lots_remaining_weight
        CHECK (remaining_weight_kg >= 0),

    CONSTRAINT chk_cherry_lots_remaining_not_exceed_initial
        CHECK (remaining_weight_kg <= initial_weight_kg)
);

CREATE INDEX idx_cherry_lots_purchase_id
    ON cherry_lots (purchase_id);

CREATE INDEX idx_cherry_lots_status
        ON cherry_lots (status);