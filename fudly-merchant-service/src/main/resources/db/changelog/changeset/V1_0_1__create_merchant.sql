CREATE TABLE merchant
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(100)             NOT NULL,
    description TEXT,
    phone       VARCHAR(100)             NOT NULL,
    email       VARCHAR(100)             NOT NULL,
    status      VARCHAR(50)              NOT NULL CHECK (status IN ('PENDING_VERIFICATION', 'ACTIVE', 'SUSPENDED', 'CLOSED')),
    owner_id    UUID                     NOT NULL,
    created     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated     timestamp WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_merchant_owner_id ON merchant (owner_id);