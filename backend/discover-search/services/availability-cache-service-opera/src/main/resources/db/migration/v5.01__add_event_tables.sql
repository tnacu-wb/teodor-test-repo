--liquibase formatted sql

--changeset Santhosh_John:01092025-v5.01__add_event_tables-1 comment:Recreates the availability_events table.
CREATE TABLE IF NOT EXISTS avail_cache.availability_events
(
    event_id     varchar(64) not null,
    event_type   varchar(32) not null,
    event_status varchar(32) not null,
    hotel_code   varchar(16) not null,
    received_on  timestamp   not null,
    event_offset int4        NOT NULL,
    CONSTRAINT event_pkey primary key (hotel_code, event_type)
);
--rollback DROP TABLE IF EXISTS avail_cache.availability_events;

--changeset Santhosh_John:01092025-v5.01__add_event_tables-2 comment:Creates the processed_events table to track event consumption.
CREATE TABLE IF NOT EXISTS avail_cache.processed_events
(
    app_key      varchar(64) not null,
    received_on  timestamp   not null,
    event_offset int4        NOT NULL,
    CONSTRAINT pro_event_pkey primary key (app_key)
);
--rollback DROP TABLE IF EXISTS avail_cache.processed_events;