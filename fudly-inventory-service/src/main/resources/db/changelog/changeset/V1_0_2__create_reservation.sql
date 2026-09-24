CREATE TABLE reservation
(
    id         UUID PRIMARY KEY,
    listing_id UUID                     NOT NULL REFERENCES inventory (listing_id),
    order_id   UUID                     NOT NULL,
    quantity   INT                      NOT NULL CHECK (quantity > 0),
    status     VARCHAR(20)              NOT NULL CHECK (status IN ('ACTIVE', 'CONFIRMED', 'RELEASED', 'EXPIRED')),
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_reservation_listing_id ON reservation (listing_id);
CREATE INDEX idx_reservation_order_id ON reservation (order_id);
CREATE INDEX idx_reservation_status_expires_at ON reservation (status, expires_at);