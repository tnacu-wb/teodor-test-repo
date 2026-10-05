-- Drop triggers that update hotel_ac.time_updated
DROP TRIGGER IF EXISTS trg_rate_update_hotel_time ON avail_cache.rate;
DROP TRIGGER IF EXISTS trg_room_update_hotel_time ON avail_cache.room;
DROP TRIGGER IF EXISTS trg_hotel_ac_update_time ON avail_cache.hotel_ac;

-- Drop associated trigger functions
DROP FUNCTION IF EXISTS update_hotel_ac_time_from_rate();
DROP FUNCTION IF EXISTS update_hotel_ac_time_from_room();
DROP FUNCTION IF EXISTS update_hotel_ac_time();
