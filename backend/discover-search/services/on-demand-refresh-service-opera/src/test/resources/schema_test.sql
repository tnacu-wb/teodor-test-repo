create schema if not exists avail_cache;

DROP TABLE IF EXISTS avail_cache.hotel_ac CASCADE;
DROP TABLE IF EXISTS avail_cache.rate CASCADE;
DROP TABLE IF EXISTS avail_cache.room CASCADE;

CREATE TABLE IF NOT EXISTS avail_cache.hotel_ac (
	id varchar(64) NOT NULL,
	avail_date timestamp NOT NULL,
	hotel_code varchar(6) NOT NULL,
	pms_source varchar(16) NULL,
	CONSTRAINT hotel_ac_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS avail_cache.room (
	id varchar(64) NOT NULL,
	quantity int4 NULL,
	type varchar(6) NULL,
	hotel_id varchar(64) NULL,
	CONSTRAINT room_pkey PRIMARY KEY (id),
	CONSTRAINT fk_room_hotel FOREIGN KEY (hotel_id) REFERENCES avail_cache.hotel_ac(id) on delete cascade
);

CREATE TABLE IF NOT EXISTS avail_cache.rate (
	id varchar(64) NOT NULL,
	amount numeric(19,2) NULL,
	premium_amt numeric(19,2) NULL,
	avail bool NULL,
	currency varchar(1) NULL,
	min_nights int4 NULL,
	max_nights int4 NULL,
	rate_category varchar(1) NULL,
	rate_code VARCHAR(32) NULL,
	hotel_id varchar(64) NULL,
	room_id VARCHAR(64) NULL,
	CONSTRAINT rate_pkey PRIMARY KEY (id),
	CONSTRAINT fk_rate_hotel FOREIGN KEY (hotel_id) REFERENCES avail_cache.hotel_ac(id) on delete cascade,
	CONSTRAINT fk_rate_room FOREIGN KEY (room_id) REFERENCES avail_cache.room(id) on delete cascade
);

CREATE TABLE IF NOT EXISTS avail_cache.hotel_migration_status (
	hotel_code varchar(6) NOT NULL,
	pms_source varchar(32) NOT NULL,
	updated_on timestamp NOT NULL,
	on_sale bool NULL,
	CONSTRAINT migration_status_pkey PRIMARY KEY (hotel_code)
);

CREATE INDEX if not exists rate_hotel_id_fk ON avail_cache.rate (hotel_id);

CREATE INDEX if not exists rate_room_id_fk ON avail_cache.rate (room_id);

CREATE INDEX if not exists room_hotel_id_fk ON avail_cache.room (hotel_id);
