--liquibase formatted sql

--changeset Santhosh_John:01092025-v5.00__drop_availability_events_table-1 comment:Drops the availability_events table.
DROP TABLE IF EXISTS avail_cache.availability_events;

--rollback CREATE TABLE avail_cache.availability_events(event_id varchar(64) not null, event_type varchar(32) not null, event_status varchar(32) not null, hotel_code varchar(16) not null, received_on timestamp not null, event_offset int4 NOT NULL, CONSTRAINT event_pkey primary key (hotel_code, event_type));