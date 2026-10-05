--liquibase formatted sql

--changeset Mircea_Caraiman:26122025-v6.10__rollback_time_updated_triggers-1 comment:Drops triggers that update hotel_ac.time_updated.
DROP TRIGGER IF EXISTS trg_rate_update_hotel_time ON avail_cache.rate;
DROP TRIGGER IF EXISTS trg_room_update_hotel_time ON avail_cache.room;
DROP TRIGGER IF EXISTS trg_hotel_ac_update_time ON avail_cache.hotel_ac;

--rollback CREATE TRIGGER trg_rate_update_hotel_time AFTER INSERT OR UPDATE ON avail_cache.rate FOR EACH ROW EXECUTE FUNCTION update_hotel_ac_time_from_rate();
--rollback CREATE TRIGGER trg_room_update_hotel_time AFTER INSERT OR UPDATE ON avail_cache.room FOR EACH ROW EXECUTE FUNCTION update_hotel_ac_time_from_room();
--rollback CREATE TRIGGER trg_hotel_ac_update_time BEFORE INSERT OR UPDATE ON avail_cache.hotel_ac FOR EACH ROW EXECUTE FUNCTION update_hotel_ac_time();

--changeset Mircea_Caraiman:26122025-v6.10__rollback_time_updated_triggers-2 comment:Drops functions that update hotel_ac.time_updated.
DROP FUNCTION IF EXISTS update_hotel_ac_time_from_rate();
DROP FUNCTION IF EXISTS update_hotel_ac_time_from_room();
DROP FUNCTION IF EXISTS update_hotel_ac_time();

--rollback N/A - Functions cannot be restored without full definition
