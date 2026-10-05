--liquibase formatted sql

--changeset Santhosh_John:01092025-v4.00__redefine_availability_events-1 comment:Makes the event_offset column mandatory (NOT NULL) in the availability_events table.
ALTER TABLE avail_cache.availability_events
    ALTER COLUMN event_offset SET NOT NULL;
--rollback ALTER TABLE avail_cache.availability_events ALTER COLUMN event_offset DROP NOT NULL;

--changeset Santhosh_John:01092025-v4.00__redefine_availability_events-2 comment:Changes the primary key of availability_events to a composite key on (hotel_code, event_type).
ALTER TABLE avail_cache.availability_events
    DROP CONSTRAINT event_pkey,
    ADD CONSTRAINT event_pkey PRIMARY KEY (hotel_code, event_type);
--rollback ALTER TABLE avail_cache.availability_events DROP CONSTRAINT event_pkey, ADD CONSTRAINT event_pkey PRIMARY KEY (event_id);