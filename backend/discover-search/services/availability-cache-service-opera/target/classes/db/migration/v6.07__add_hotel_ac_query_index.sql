--liquibase formatted sql

--changeset Mircea_Caraiman:26122025-v6.03__add_hotel_ac_query_index-1 comment:Adds composite index on hotel_ac table for queries filtering by pms_source, hotel_code, and avail_date to improve query performance.
CREATE INDEX IF NOT EXISTS idx_hotel_ac_pms_hotel_date 
ON avail_cache.hotel_ac (pms_source, hotel_code, avail_date);

--rollback DROP INDEX IF EXISTS avail_cache.idx_hotel_ac_pms_hotel_date;



