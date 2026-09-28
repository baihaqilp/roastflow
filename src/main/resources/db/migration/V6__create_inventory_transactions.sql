CREATE TABLE inventory_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    lot_id UUID NOT NULL,
    lot_type VARCHAR(30) NOT NULL,

    transaction_type VARCHAR(30) NOT NULL,

    quantity_kg NUMERIC(15,3) NOT NULL,

    reference_type VARCHAR(50),
    reference_id UUID,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_inventory_transaction_quantity
        CHECK (quantity_kg > 0)
);

CREATE INDEX idx_inventory_transactions_lot_id
    ON inventory_transactions (lot_id);

CREATE INDEX idx_inventory_transactions_reference
    ON inventory_transactions (reference_type, reference_id);

CREATE INDEX idx_inventory_transactions_created_at
    ON inventory_transactions (created_at);