--liquibase formatted sql

--changeset Santhosh_John:01092025-v2.00__refactor_availability_schema-1 comment:Drops the obsolete test1 table.
DROP TABLE IF EXISTS avail_cache.test1;
--rollback CREATE TABLE avail_cache.test1 (id varchar(26) NOT NULL, name varchar(26) NULL);

--changeset Santhosh_John:01092025-v2.00__refactor_availability_schema-2 comment:Widens hotel_ac.id and adds the pms_source column.
ALTER TABLE avail_cache.hotel_ac
    ADD COLUMN pms_source VARCHAR(16),
    ALTER COLUMN id TYPE VARCHAR(64);
--rollback ALTER TABLE avail_cache.hotel_ac DROP COLUMN pms_source;
--rollback ALTER TABLE avail_cache.hotel_ac ALTER COLUMN id TYPE VARCHAR(17);

--changeset Santhosh_John:01092025-v2.00__refactor_availability_schema-3 comment:Widens id and hotel_id columns in the room table for consistency.
ALTER TABLE avail_cache.room
    ALTER COLUMN id TYPE VARCHAR(64),
    ALTER COLUMN hotel_id TYPE VARCHAR(64);
--rollback ALTER TABLE avail_cache.room ALTER COLUMN id TYPE VARCHAR(26);
--rollback ALTER TABLE avail_cache.room ALTER COLUMN hotel_id TYPE VARCHAR(17);

--changeset Santhosh_John:01092025-v2.00__refactor_availability_schema-4 comment:Refactors the rate table with new columns, wider IDs, and a column rename (rate -> rate_category).
ALTER TABLE avail_cache.rate
    RENAME COLUMN rate TO rate_category;
ALTER TABLE avail_cache.rate
    ADD COLUMN rate_code VARCHAR(32),
    ADD COLUMN room_id   VARCHAR(64),
    ALTER COLUMN id TYPE VARCHAR(64),
    ALTER COLUMN hotel_id TYPE VARCHAR(64);
--rollback ALTER TABLE avail_cache.rate RENAME COLUMN rate_category TO rate;
--rollback ALTER TABLE avail_cache.rate DROP COLUMN rate_code;
--rollback ALTER TABLE avail_cache.rate DROP COLUMN room_id;
--rollback ALTER TABLE avail_cache.rate ALTER COLUMN id TYPE VARCHAR(26);
--rollback ALTER TABLE avail_cache.rate ALTER COLUMN hotel_id TYPE VARCHAR(17);

--changeset Santhosh_John:01092025-v2.00__refactor_availability_schema-5 comment:Adds an index to the new room_id column on the rate table.
CREATE INDEX IF NOT EXISTS rate_room_id_fk ON avail_cache.rate (room_id);
--rollback DROP INDEX IF EXISTS avail_cache.rate_room_id_fk;

--changeset Santhosh_John:01092025-v2.00__refactor_availability_schema-6 comment:Creates a new table to track availability-related events.
CREATE TABLE IF NOT EXISTS avail_cache.availability_events
(
    event_id     VARCHAR(64) NOT NULL,
    event_type   VARCHAR(32) NOT NULL,
    event_status VARCHAR(32) NOT NULL,
    hotel_code   VARCHAR(16) NOT NULL,
    received_on  TIMESTAMP   NOT NULL,
    event_offset INT4,
    CONSTRAINT event_pkey PRIMARY KEY (event_id)
);
--rollback DROP TABLE IF EXISTS avail_cache.availability_events;

--changeset Santhosh_John:01092025-v2.00__refactor_availability_schema-7 comment:Creates a new table to track hotel PMS migration status.
CREATE TABLE IF NOT EXISTS avail_cache.hotel_migration_status
(
    hotel_code VARCHAR(6)  NOT NULL,
    pms_source VARCHAR(32) NOT NULL,
    updated_on TIMESTAMP   NOT NULL,
    on_sale    BOOL        NULL,
    CONSTRAINT migration_status_pkey PRIMARY KEY (hotel_code)
);
--rollback DROP TABLE IF EXISTS avail_cache.hotel_migration_status;