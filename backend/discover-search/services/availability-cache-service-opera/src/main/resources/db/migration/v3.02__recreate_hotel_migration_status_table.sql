--liquibase formatted sql

--changeset Santhosh_John:01092025-v3.02__recreate_hotel_migration_status_table-1 comment:Recreates the hotel_migration_status table again - maybe change in requirements.
CREATE TABLE IF NOT EXISTS avail_cache.hotel_migration_status
(
    hotel_code varchar(6)  NOT NULL,
    pms_source varchar(32) NOT NULL,
    updated_on timestamp   NOT NULL,
    on_sale    bool        NULL,
    CONSTRAINT migration_status_pkey PRIMARY KEY (hotel_code)
);

--rollback DROP TABLE IF EXISTS avail_cache.hotel_migration_status;