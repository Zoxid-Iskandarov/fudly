CREATE TABLE outbox_event
(
    id              UUID PRIMARY KEY,
    aggregate_type  VARCHAR(50)              NOT NULL,
    aggregate_id    UUID                     NOT NULL,
    event_type      VARCHAR(100)             NOT NULL,
    payload         JSONB                    NOT NULL,
    status          VARCHAR(20)              NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'PROCESSING', 'SENT', 'FAILED')),
    created         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_attempt_at TIMESTAMP WITH TIME ZONE,
    sent_at         TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_outbox_event_status_created ON outbox_event (status, created);