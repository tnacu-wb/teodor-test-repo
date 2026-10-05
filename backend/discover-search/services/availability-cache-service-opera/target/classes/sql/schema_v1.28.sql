-- Drop triggers that update hotel_ac.time_updated (must be dropped before functions)
DROP TRIGGER IF EXISTS trg_rate_update_hotel_time ON avail_cache.rate;
DROP TRIGGER IF EXISTS trg_room_update_hotel_time ON avail_cache.room;
DROP TRIGGER IF EXISTS trg_hotel_ac_update_time ON avail_cache.hotel_ac;

-- Drop functions that update hotel_ac.time_updated
DROP FUNCTION IF EXISTS update_hotel_ac_time_from_rate();
DROP FUNCTION IF EXISTS update_hotel_ac_time_from_room();
DROP FUNCTION IF EXISTS update_hotel_ac_time();

-- Drop time_updated column from hotel_ac table
ALTER TABLE avail_cache.hotel_ac DROP COLUMN time_updated;

-- Drop composite index on hotel_ac table
DROP INDEX IF EXISTS avail_cache.idx_hotel_ac_pms_hotel_date;

