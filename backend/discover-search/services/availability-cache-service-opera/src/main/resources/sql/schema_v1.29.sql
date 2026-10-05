-- Add composite index on hotel_ac table for queries filtering by pms_source, hotel_code, and avail_date
CREATE INDEX IF NOT EXISTS idx_hotel_ac_pms_hotel_date 
ON avail_cache.hotel_ac (pms_source, hotel_code, avail_date);

-- Add time_updated timestamp column to hotel_ac table
ALTER TABLE avail_cache.hotel_ac
    ADD COLUMN time_updated TIMESTAMP;

-- Create trigger function to update hotel_ac.time_updated when rate table is modified
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

-- Create trigger on rate table
CREATE TRIGGER trg_rate_update_hotel_time
AFTER INSERT OR UPDATE ON avail_cache.rate
FOR EACH ROW
EXECUTE FUNCTION update_hotel_ac_time_from_rate();

-- Create trigger function to update hotel_ac.time_updated when room table is modified
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

-- Create trigger on room table
CREATE TRIGGER trg_room_update_hotel_time
AFTER INSERT OR UPDATE ON avail_cache.room
FOR EACH ROW
EXECUTE FUNCTION update_hotel_ac_time_from_room();

-- Create trigger function to update hotel_ac.time_updated when hotel_ac table is modified directly
CREATE OR REPLACE FUNCTION update_hotel_ac_time()
RETURNS TRIGGER AS $$
BEGIN
    NEW.time_updated := timezone('UTC', CURRENT_TIMESTAMP);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger on hotel_ac table
CREATE TRIGGER trg_hotel_ac_update_time
BEFORE INSERT OR UPDATE ON avail_cache.hotel_ac
FOR EACH ROW
EXECUTE FUNCTION update_hotel_ac_time();

