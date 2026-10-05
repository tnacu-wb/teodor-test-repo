--liquibase formatted sql

--changeset Mircea_Caraiman:26122025-v6.04__add_time_updated_to_hotel_ac-1 comment:Adds time_updated timestamp column to hotel_ac table to track when hotel availability data was last modified.
ALTER TABLE avail_cache.hotel_ac
    ADD COLUMN time_updated TIMESTAMP;

--rollback ALTER TABLE avail_cache.hotel_ac DROP COLUMN time_updated;


