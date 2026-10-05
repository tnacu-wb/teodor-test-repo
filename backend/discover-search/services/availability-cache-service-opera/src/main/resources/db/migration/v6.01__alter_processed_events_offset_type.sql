--liquibase formatted sql

--changeset Santhosh_John:01092025-v6.01__alter_processed_events_offset_type-1 comment:Changes the data type of event_offset in processed_events to BIGINT to support larger/amount values.
ALTER TABLE avail_cache.processed_events ALTER COLUMN event_offset TYPE BIGINT;

--rollback ALTER TABLE avail_cache.processed_events ALTER COLUMN event_offset TYPE INT4;