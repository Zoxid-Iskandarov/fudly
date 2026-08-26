CREATE TABLE branch
(
    id            UUID PRIMARY KEY,
    merchant_id   UUID                     NOT NULL REFERENCES merchant (id) ON DELETE CASCADE,
    address       VARCHAR(255)             NOT NULL,
    latitude      DOUBLE PRECISION         NOT NULL,
    longitude     DOUBLE PRECISION         NOT NULL,
    opening_hours JSONB,
    status        VARCHAR(50)              NOT NULL CHECK (status IN ('OPEN', 'TEMPORARILY_CLOSED', 'CLOSED')),
    created       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_branch_merchant_id ON branch (merchant_id);
CREATE INDEX idx_branch_latitude_longitude ON branch (latitude, longitude);