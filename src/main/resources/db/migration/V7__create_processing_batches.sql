CREATE TABLE processing_batches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    cherry_lot_id UUID NOT NULL,

    batch_code VARCHAR(50) NOT NULL,

    processing_method VARCHAR(30) NOT NULL,

    input_weight_kg NUMERIC(15,3) NOT NULL,

    start_date DATE NOT NULL,

    drying_start_date DATE,

    drying_end_date DATE,

    output_green_bean_kg NUMERIC(15,3),

    status VARCHAR(30) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_processing_batches_batch_code
        UNIQUE (batch_code),

    CONSTRAINT fk_processing_batches_cherry_lot
        FOREIGN KEY (cherry_lot_id)
        REFERENCES cherry_lots(id),

    CONSTRAINT chk_processing_batches_input_weight
        CHECK (input_weight_kg > 0),

    CONSTRAINT chk_processing_batches_output_weight
        CHECK (
            output_green_bean_kg IS NULL
            OR output_green_bean_kg >= 0
        )
);

CREATE INDEX idx_processing_batches_cherry_lot_id
    ON processing_batches(cherry_lot_id);

CREATE INDEX idx_processing_batches_status
    ON processing_batches(status);

CREATE INDEX idx_processing_batches_processing_method
    ON processing_batches(processing_method);

CREATE INDEX idx_processing_batches_start_date
    ON processing_batches(start_date);