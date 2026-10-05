--liquibase formatted sql

--changeset Mircea_Caraiman:26122025-v6.05__add_time_updated_triggers-1 comment:Creates trigger function to update hotel_ac.time_updated when rate table is modified.
CREATE OR REPLACE FUNCTION update_hotel_ac_time_from_rate()
RETURNS TRIGGER AS $$
DECLARE
    v_hotel_id VARCHAR(64);
BEGIN
    -- First try to get hotel_id directly from rate
    IF NEW.hotel_id IS NOT NULL THEN
        v_hotel_id := NEW.hotel_id;
    -- Otherwise, get it through room relationship
    ELSIF NEW.room_id IS NOT NULL THEN
        SELECT hotel_id INTO v_hotel_id 
        FROM avail_cache.room 
        WHERE id = NEW.room_id;
    END IF;
    
    -- Update hotel_ac if we found a hotel_id
    IF v_hotel_id IS NOT NULL THEN
        UPDATE avail_cache.hotel_ac 
        SET time_updated = timezone('UTC', CURRENT_TIMESTAMP) 
        WHERE id = v_hotel_id;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

--rollback DROP FUNCTION IF EXISTS update_hotel_ac_time_from_rate();

--changeset Mircea_Caraiman:26122025-v6.05__add_time_updated_triggers-2 comment:Creates trigger on rate table to automatically update hotel_ac.time_updated.
CREATE TRIGGER trg_rate_update_hotel_time
AFTER INSERT OR UPDATE ON avail_cache.rate
FOR EACH ROW
EXECUTE FUNCTION update_hotel_ac_time_from_rate();

--rollback DROP TRIGGER IF EXISTS trg_rate_update_hotel_time ON avail_cache.rate;

--changeset Mircea_Caraiman:26122025-v6.05__add_time_updated_triggers-3 comment:Creates trigger function to update hotel_ac.time_updated when room table is modified.
CREATE OR REPLACE FUNCTION update_hotel_ac_time_from_room()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.hotel_id IS NOT NULL THEN
        UPDATE avail_cache.hotel_ac 
        SET time_updated = timezone('UTC', CURRENT_TIMESTAMP) 
        WHERE id = NEW.hotel_id;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

--rollback DROP FUNCTION IF EXISTS update_hotel_ac_time_from_room();

--changeset Mircea_Caraiman:26122025-v6.05__add_time_updated_triggers-4 comment:Creates trigger on room table to automatically update hotel_ac.time_updated.
CREATE TRIGGER trg_room_update_hotel_time
AFTER INSERT OR UPDATE ON avail_cache.room
FOR EACH ROW
EXECUTE FUNCTION update_hotel_ac_time_from_room();

--rollback DROP TRIGGER IF EXISTS trg_room_update_hotel_time ON avail_cache.room;

--changeset Mircea_Caraiman:26122025-v6.05__add_time_updated_triggers-5 comment:Creates trigger function to update hotel_ac.time_updated when hotel_ac table is modified directly.
CREATE OR REPLACE FUNCTION update_hotel_ac_time()
RETURNS TRIGGER AS $$
BEGIN
    NEW.time_updated := timezone('UTC', CURRENT_TIMESTAMP);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

--rollback DROP FUNCTION IF EXISTS update_hotel_ac_time();

--changeset Mircea_Caraiman:26122025-v6.05__add_time_updated_triggers-6 comment:Creates trigger on hotel_ac table to automatically update time_updated on insert or update.
CREATE TRIGGER trg_hotel_ac_update_time
BEFORE INSERT OR UPDATE ON avail_cache.hotel_ac
FOR EACH ROW
EXECUTE FUNCTION update_hotel_ac_time();

--rollback DROP TRIGGER IF EXISTS trg_hotel_ac_update_time ON avail_cache.hotel_ac;

