--liquibase formatted sql

--changeset Santhosh_John:01092025-v3.01__add_foreign_key_rate_to_room-1 comment:Adds a foreign key constraint to link the rate table to the room table for data integrity.
ALTER TABLE avail_cache.rate
    ADD CONSTRAINT fk_rate_room
        FOREIGN KEY (room_id) REFERENCES avail_cache.room (id) ON DELETE CASCADE;

--rollback ALTER TABLE avail_cache.rate DROP CONSTRAINT fk_rate_room;