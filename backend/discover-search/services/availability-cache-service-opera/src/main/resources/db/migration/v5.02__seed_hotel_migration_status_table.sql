--liquibase formatted sql

--changeset Santhosh_John:01092025-v5.02__seed_hotel_migration_status_table-1 comment:Inserts initial seed data for specific hotels into the migration status table.
INSERT INTO avail_cache.hotel_migration_status (hotel_code, pms_source, updated_on, on_sale) VALUES ('DRECIT','OPERA', now(), true)
ON CONFLICT ON CONSTRAINT migration_status_pkey DO NOTHING;

INSERT INTO avail_cache.hotel_migration_status (hotel_code, pms_source, updated_on, on_sale) VALUES ('BIRTOB','OPERA', now(), true)
ON CONFLICT ON CONSTRAINT migration_status_pkey DO NOTHING;

--rollback DELETE FROM avail_cache.hotel_migration_status WHERE hotel_code IN ('DRECIT', 'BIRTOB');