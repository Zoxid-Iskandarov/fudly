CREATE TABLE listing
(
    id               UUID PRIMARY KEY,
    branch_id        UUID                     NOT NULL,
    title            VARCHAR(150)             NOT NULL,
    description      TEXT,
    original_price   NUMERIC(10, 2)           NOT NULL CHECK (original_price > 0),
    discounted_price NUMERIC(10, 2)           NOT NULL CHECK (discounted_price > 0),
    quantity         INT                      NOT NULL CHECK (quantity > 0),
    expiration_time  TIMESTAMP WITH TIME ZONE NOT NULL,
    status           VARCHAR(50)              NOT NULL
        CHECK (status IN ('DRAFT', 'ACTIVE', 'RESERVED', 'SOLD_OUT', 'EXPIRED', 'CANCELLED')),
    version          INT                      NOT NULL DEFAULT 0,
    created          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_discount CHECK (discounted_price < original_price)
);

CREATE INDEX idx_listing_branch_id ON listing (branch_id);
CREATE INDEX idx_listing_status ON listing (status);