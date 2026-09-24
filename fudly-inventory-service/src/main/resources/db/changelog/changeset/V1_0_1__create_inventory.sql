CREATE TABLE inventory
(
    listing_id         UUID PRIMARY KEY,
    available_quantity INT NOT NULL CHECK (available_quantity >= 0),
    reserved_quantity  INT NOT NULL DEFAULT 0 CHECK (reserved_quantity >= 0),
    version            INT NOT NULL DEFAULT 0
);