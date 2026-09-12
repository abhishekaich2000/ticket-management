-- ticket status size alter

ALTER TABLE tickets ALTER COLUMN status TYPE VARCHAR(40);
ALTER TABLE tickets ALTER COLUMN priority TYPE VARCHAR(40);
ALTER TABLE tickets ALTER COLUMN category TYPE VARCHAR(40);