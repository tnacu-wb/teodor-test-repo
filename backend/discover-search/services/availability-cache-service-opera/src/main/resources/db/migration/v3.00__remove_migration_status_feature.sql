--liquibase formatted sql

--changeset Santhosh_John:01092025-v3.00__remove_migration_status_feature-1 comment:Drops the hotel_migration_status table.
DROP TABLE IF EXISTS avail_cache.hotel_migration_status;

--rollback CREATE TABLE avail_cache.hotel_migration_status (hotel_code VARCHAR(6) NOT NULL, pms_source VARCHAR(32) NOT NULL, updated_on TIMESTAMP NOT NULL, on_sale BOOL NULL, CONSTRAINT migration_status_pkey PRIMARY KEY (hotel_code));