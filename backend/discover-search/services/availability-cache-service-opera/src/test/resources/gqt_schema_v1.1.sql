create schema if not exists avail_cache;

DROP TABLE IF EXISTS avail_cache.hotel_ac CASCADE;
DROP TABLE IF EXISTS avail_cache.rate CASCADE;
DROP TABLE IF EXISTS avail_cache.room CASCADE;

DROP TABLE IF EXISTS avail_cache.test1;

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
         event_offset int4,
         CONSTRAINT event_pkey primary key (event_id)
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

insert into avail_cache.hotel_ac (id, avail_date, hotel_code, pms_source) values ('MANOLD_2023-03-28_OPERA', '2023-03-28', 'MANOLD', 'OPERA');
insert into avail_cache.hotel_ac (id, avail_date, hotel_code, pms_source) values ('LONEUS_2023-03-28_OPERA', '2023-03-28', 'LONEUS', 'OPERA');
insert into avail_cache.hotel_ac (id, avail_date, hotel_code, pms_source) values ('IPSMTI_2023-03-29_OPERA', '2023-03-29', 'IPSMTI', 'OPERA');
insert into avail_cache.hotel_ac (id, avail_date, hotel_code, pms_source) values ('NEWTHY_2023-03-29_OPERA', '2023-03-29', 'NEWTHY', 'OPERA');


insert into avail_cache.room (id, quantity, type, hotel_id) values ('SB_MANOLD_2023-03-28_OPERA',8,'SB','MANOLD_2023-03-28_OPERA');
insert into avail_cache.room (id, quantity, type, hotel_id) values ('PRE_MANOLD_2023-03-28_OPERA',9,'PRE','MANOLD_2023-03-28_OPERA');

insert into avail_cache.room (id, quantity, type, hotel_id) values ('DB_LONEUS_2023-03-28_OPERA',4,'DB','LONEUS_2023-03-28_OPERA');

insert into avail_cache.room (id, quantity, type, hotel_id) values ('FAM_LONEUS_2023-03-28_OPERA',0,'FAM','IPSMTI_2023-03-29_OPERA');

insert into avail_cache.room (id, quantity, type, hotel_id) values ('FAM_NEWTHY_2023-03-29_OPERA',0,'FAM','NEWTHY_2023-03-29_OPERA');
insert into avail_cache.room (id, quantity, type, hotel_id) values ('TWIN_NEWTHY_2023-03-29_OPERA',2,'TWIN','NEWTHY_2023-03-29_OPERA');


insert into avail_cache.rate (id, avail, rate_code, rate_category, amount, currency, min_nights, max_nights, hotel_id, room_id)
values
('S_MANOLD_2023-03-28_OPERA_SB', true, 'ADVANCE', 'S', 100.00, 'G', 0, 3, 'MANOLD_2023-03-28_OPERA', 'SB_MANOLD_2023-03-28_OPERA'),
('U_MANOLD_2023-03-28_OPERA_SB', true, 'STANDARD', 'U', 90.00, 'G', 1, 6, 'MANOLD_2023-03-28_OPERA', 'SB_MANOLD_2023-03-28_OPERA'),
('F_MANOLD_2023-03-28_OPERA_PRE', true, 'BUSIFLEX', 'F', 80.00, 'G', 1, 5, 'MANOLD_2023-03-28_OPERA', 'PRE_MANOLD_2023-03-28_OPERA'),
('S_LONEUS_2023-03-28_OPERA_DB', true, 'ADVANCE', 'S', 50.00, 'G', 0, 3, 'LONEUS_2023-03-28_OPERA', 'DB_LONEUS_2023-03-28_OPERA'),
('S_IPSMTI_2023-03-29_OPERA_FAM', true, 'ADVANCE', 'S', 20.00, 'G', 0, 3, 'IPSMTI_2023-03-29_OPERA', 'FAM_LONEUS_2023-03-28_OPERA'),
('S_NEWTHY_2023-03-29_OPERA_FAM', true, 'ADVANCE', 'S', 80.00, 'G', 0, 3, 'NEWTHY_2023-03-29_OPERA', 'FAM_NEWTHY_2023-03-29_OPERA'),
('S_NEWTHY_2023-03-29_OPERA_TWIN', true, 'ADVANCE', 'S', 70.00, 'G', 0, 3, 'NEWTHY_2023-03-29_OPERA', 'TWIN_NEWTHY_2023-03-29_OPERA'),
('F_NEWTHY_2023-03-29_OPERA_TWIN', true, 'BUSIFLEX', 'F', 80.00, 'G', 1, 7, 'NEWTHY_2023-03-29_OPERA', 'TWIN_NEWTHY_2023-03-29_OPERA');
