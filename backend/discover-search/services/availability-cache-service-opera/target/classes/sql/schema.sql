create schema if not exists avail_cache;

-- DROP TABLE avail_cache.hotel_ac;
-- DROP TABLE avail_cache.rate;
-- DROP TABLE avail_cache.room;

CREATE TABLE IF NOT EXISTS avail_cache.hotel_ac (
	id varchar(17) NOT NULL,
	avail_date timestamp NOT NULL,
	hotel_code varchar(6) NOT NULL,
	CONSTRAINT hotel_ac_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS avail_cache.rate (
	id varchar(26) NOT NULL,
	amount numeric(19,2) NULL,
	avail bool NULL,
	currency varchar(1) NULL,
	min_nights int4 NULL,
	rate varchar(1) NULL,
	hotel_id varchar(17) NULL,
	max_nights int4 NULL,
	premium_amt numeric(19,2) NULL,
	CONSTRAINT rate_pkey PRIMARY KEY (id),
	CONSTRAINT fk_rate_hotel FOREIGN KEY (hotel_id) REFERENCES avail_cache.hotel_ac(id) on delete cascade
);

CREATE TABLE IF NOT EXISTS avail_cache.room (
	id varchar(26) NOT NULL,
	quantity int4 NULL,
	type varchar(6) NULL,
	hotel_id varchar(17) NULL,
	CONSTRAINT room_pkey PRIMARY KEY (id),
	CONSTRAINT fk_room_hotel FOREIGN KEY (hotel_id) REFERENCES avail_cache.hotel_ac(id) on delete cascade
);

CREATE TABLE IF NOT EXISTS avail_cache.test1 (id varchar(26) NOT NULL,
        name varchar(26) NULL);

CREATE TABLE IF NOT EXISTS avail_cache.hotel_location(
        hotel_code varchar(6) not null,
        place_id varchar(64) not null,
        constraint hotel_location_pkey primary key (hotel_code, place_id)
);

CREATE TABLE IF NOT EXISTS avail_cache.location_price(
         place_id varchar(64) not null,
         avail_date date not null,
         currency varchar(1),
         price numeric(10,2),
         constraint location_price_pkey primary key (place_id, avail_date)
);

CREATE INDEX if not exists rate_hotel_id_fk ON avail_cache.rate (hotel_id);

CREATE INDEX if not exists room_hotel_id_fk ON avail_cache.room (hotel_id);