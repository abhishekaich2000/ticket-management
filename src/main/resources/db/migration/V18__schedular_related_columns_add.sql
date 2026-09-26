-- adding enabled in schedulars tables and is_sla_breached in tickets table

ALTER TABLE schedulars
    ADD COLUMN IF NOT EXISTS enabled BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE tickets
    ADD COLUMN IF NOT EXISTS is_sla_breached BOOLEAN NOT NULL DEFAULT FALSE;