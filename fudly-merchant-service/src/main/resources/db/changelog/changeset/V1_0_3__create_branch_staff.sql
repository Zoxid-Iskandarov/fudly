CREATE TABLE branch_staff
(
    branch_id UUID                     NOT NULL REFERENCES branch (id) ON DELETE CASCADE,
    user_id   UUID                     NOT NULL,
    added     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (branch_id, user_id)
);