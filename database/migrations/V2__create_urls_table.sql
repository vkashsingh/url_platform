-- V2__create_urls_table.sql
-- Create urls table for the URL Platform

CREATE TABLE urls (
    id           BIGSERIAL PRIMARY KEY,
    original_url TEXT                     NOT NULL,
    short_code   VARCHAR(10)              NOT NULL,
    user_id      BIGINT                   NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_urls_short_code UNIQUE (short_code),
    CONSTRAINT fk_urls_user_id   FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_urls_short_code ON urls (short_code);
CREATE INDEX idx_urls_user_id    ON urls (user_id);
