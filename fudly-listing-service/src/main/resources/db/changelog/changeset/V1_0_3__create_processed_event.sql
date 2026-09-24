CREATE TABLE processed_event
(
    event_id      UUID                     NOT NULL,
    consumer_name VARCHAR(100)             NOT NULL,
    processed_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (event_id, consumer_name)
);