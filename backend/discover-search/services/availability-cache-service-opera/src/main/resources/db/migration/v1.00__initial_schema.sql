--liquibase formatted sql

--changeset Santhosh_John:01092025-v1.00__initial_schema-1 comment:Creates the avail_cache schema for the service.
CREATE SCHEMA IF NOT EXISTS avail_cache;
--rollback DROP SCHEMA IF EXISTS avail_cache CASCADE;

--changeset Santhosh_John:01092025-v1.00__initial_schema-2 comment:Base table for hotel availability.
CREATE TABLE IF NOT EXISTS avail_cache.hotel_ac
(
    id         varchar(17) NOT NULL,
    avail_date timestamp   NOT NULL,
    hotel_code varchar(6)  NOT NULL,
    CONSTRAINT hotel_ac_pkey PRIMARY KEY (id)
);
--rollback DROP TABLE IF EXISTS avail_cache.hotel_ac;

--changeset Santhosh_John:01092025-v1.00__initial_schema-3 comment:Table to store pricing and rate details. Depends on hotel_ac.
CREATE TABLE IF NOT EXISTS avail_cache.rate
(
    id          varchar(26)    NOT NULL,
    amount      numeric(19, 2) NULL,
    avail       bool           NULL,
    currency    varchar(1)     NULL,
    min_nights  int4           NULL,
    rate        varchar(1)     NULL,
    hotel_id    varchar(17)    NULL,
    max_nights  int4           NULL,
    premium_amt numeric(19, 2) NULL,
    CONSTRAINT rate_pkey PRIMARY KEY (id),
    CONSTRAINT fk_rate_hotel FOREIGN KEY (hotel_id) REFERENCES avail_cache.hotel_ac (id) ON DELETE CASCADE
);
--rollback DROP TABLE IF EXISTS avail_cache.rate;

--changeset Santhosh_John:01092025-v1.00__initial_schema-4 comment:Table to store room availability and types. Depends on hotel_ac.
CREATE TABLE IF NOT EXISTS avail_cache.room
(
    id       varchar(26) NOT NULL,
    quantity int4        NULL,
    type     varchar(6)  NULL,
    hotel_id varchar(17) NULL,
    CONSTRAINT room_pkey PRIMARY KEY (id),
    CONSTRAINT fk_room_hotel FOREIGN KEY (hotel_id) REFERENCES avail_cache.hotel_ac (id) ON DELETE CASCADE
);
--rollback DROP TABLE IF EXISTS avail_cache.room;

--changeset Santhosh_John:01092025-v1.00__initial_schema-5 comment:A temporary or test table.
CREATE TABLE IF NOT EXISTS avail_cache.test1
(
    id   varchar(26) NOT NULL,
    name varchar(26) NULL
);
--rollback DROP TABLE IF EXISTS avail_cache.test1;

--changeset Santhosh_John:01092025-v1.00__initial_schema-6 comment:Maps hotel codes to geographic place IDs.
CREATE TABLE IF NOT EXISTS avail_cache.hotel_location
(
    hotel_code varchar(6)  NOT NULL,
    place_id   varchar(64) NOT NULL,
    CONSTRAINT hotel_location_pkey PRIMARY KEY (hotel_code, place_id)
);
--rollback DROP TABLE IF EXISTS avail_cache.hotel_location;

--changeset Santhosh_John:01092025-v1.00__initial_schema-7 comment:Stores aggregated prices for locations.
CREATE TABLE IF NOT EXISTS avail_cache.location_price
(
    place_id   varchar(64) NOT NULL,
    avail_date date        NOT NULL,
    currency   varchar(1),
    price      numeric(10, 2),
    CONSTRAINT location_price_pkey PRIMARY KEY (place_id, avail_date)
);
--rollback DROP TABLE IF EXISTS avail_cache.location_price;

--changeset Santhosh_John:01092025-v1.00__initial_schema-8 comment:Adds foreign key indexes to improve join performance.
CREATE INDEX IF NOT EXISTS rate_hotel_id_fk ON avail_cache.rate (hotel_id);
CREATE INDEX IF NOT EXISTS room_hotel_id_fk ON avail_cache.room (hotel_id);
--rollback DROP INDEX IF EXISTS avail_cache.rate_hotel_id_fk;
--rollback DROP INDEX IF EXISTS avail_cache.room_hotel_id_fk;