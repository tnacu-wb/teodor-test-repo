create schema if not exists avail_cache;

--DROP TABLE IF EXISTS avail_cache.hotel_ac CASCADE;
--DROP TABLE IF EXISTS avail_cache.rate CASCADE;
--DROP TABLE IF EXISTS avail_cache.room CASCADE;
--DROP TABLE IF EXISTS avail_cache.test1;
--DROP TABLE IF EXISTS avail_cache.availability_events;
--DROP TABLE IF EXISTS avail_cache.processed_events;

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

CREATE TABLE IF NOT EXISTS avail_cache.availability_events(
         event_id varchar(64) not null,
         event_type varchar(32) not null,
         event_status varchar(32) not null,
         hotel_code varchar(16) not null,
         received_on timestamp not null,
         event_offset int4 NOT NULL,
         CONSTRAINT event_pkey primary key (hotel_code, event_type)
);

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

CREATE TABLE IF NOT EXISTS avail_cache.hotel_migration_status (
	hotel_code varchar(6) NOT NULL,
	pms_source varchar(32) NOT NULL,
	updated_on timestamp NOT NULL,
	on_sale bool NULL,
	CONSTRAINT migration_status_pkey PRIMARY KEY (hotel_code)
);

CREATE TABLE IF NOT EXISTS avail_cache.processed_events(
         app_key varchar(64) not null,
         received_on timestamp not null,
         event_offset BIGINT NOT NULL,
         CONSTRAINT pro_event_pkey primary key (app_key)
);

CREATE INDEX if not exists rate_hotel_id_fk ON avail_cache.rate (hotel_id);

CREATE INDEX if not exists rate_room_id_fk ON avail_cache.rate (room_id);

CREATE INDEX if not exists room_hotel_id_fk ON avail_cache.room (hotel_id);

-- DROP procedure IF EXISTS avail_cache.UPDATE_LOCATION_PRICE;

CREATE OR REPLACE PROCEDURE avail_cache.UPDATE_LOCATION_PRICE(
  v_avail_date DATE,
  v_hotel_code varchar(10)
)
AS '
declare
    v_place_id record;
    location_price_record record;
begin
    for v_place_id in select distinct hl.place_id from avail_cache.hotel_location hl where hl.hotel_code = v_hotel_code
    loop
        select min(ra.amount) price, ra.currency into location_price_record
            from avail_cache.hotel_ac ha
            inner join avail_cache.rate ra on (ra.hotel_id=ha.id)
            where ha.hotel_code in (select hotel_code from avail_cache.hotel_location where place_id =  v_place_id.place_id)
            and ha.avail_date = v_avail_date
            group by ra.currency
            having (min(ra.amount) is not null) and min(ra.amount)>0;
        insert into avail_cache.location_price values
         (v_place_id.place_id, v_avail_date, location_price_record.currency, location_price_record.price)
         on conflict on constraint location_price_pkey do update set currency=location_price_record.currency,
         price=location_price_record.price;
     end loop;
end;
' LANGUAGE PLPGSQL;

CREATE OR REPLACE PROCEDURE avail_cache.lambda_user_creation()
AS
'DECLARE
 BEGIN
   IF EXISTS (SELECT FROM pg_user WHERE usename = ''lambda_postgres'')
   THEN
     RAISE NOTICE ''SKIP ROLE AND USERNAME CREATION!'';
   ELSE
     BEGIN
      IF EXISTS (SELECT FROM pg_roles WHERE rolname = ''lambda_postgres'')
      THEN
        RAISE NOTICE ''SKIP ROLE CREATION!'';
   ELSE
     BEGIN
       CREATE ROLE readwrite;
       GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA avail_cache TO readwrite;
	     CREATE USER lambda_postgres WITH LOGIN;
	     GRANT readwrite TO lambda_postgres;
       GRANT rds_iam TO lambda_postgres;
       GRANT CREATE ON SCHEMA avail_cache TO lambda_postgres;
       GRANT USAGE ON SCHEMA avail_cache TO lambda_postgres;
       EXCEPTION WHEN duplicate_object THEN RAISE NOTICE ''%, moving to next statement'', SQLERRM USING ERRCODE = SQLSTATE;
     END;
     END IF;
     END;
   END IF;
 END;
' LANGUAGE PLPGSQL;

CALL avail_cache.lambda_user_creation();

INSERT INTO avail_cache.hotel_migration_status (hotel_code, pms_source, updated_on, on_sale) VALUES ('DRECIT','OPERA', now(), true)
ON CONFLICT ON CONSTRAINT migration_status_pkey DO NOTHING;

INSERT INTO avail_cache.hotel_migration_status (hotel_code, pms_source, updated_on, on_sale) VALUES ('BIRTOB','OPERA', now(), true)
ON CONFLICT ON CONSTRAINT migration_status_pkey DO NOTHING;

