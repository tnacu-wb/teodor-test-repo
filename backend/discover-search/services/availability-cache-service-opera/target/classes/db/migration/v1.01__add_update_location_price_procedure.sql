--liquibase formatted sql

--changeset Santhosh_John:01092025-v1.01__add_update_location_price_procedure-1 comment:Adds a stored procedure to calculate and update aggregated location prices based on hotel availability.
CREATE OR REPLACE PROCEDURE avail_cache.UPDATE_LOCATION_PRICE(
    v_avail_date DATE,
    v_hotel_code varchar(10)
)
AS '
    declare
        v_place_id            record;
        location_price_record record;
    begin
        for v_place_id in select distinct hl.place_id
                          from avail_cache.hotel_location hl
                          where hl.hotel_code = v_hotel_code
            loop
                select min(ra.amount) price,
                       ra.currency
                into location_price_record
                from avail_cache.hotel_ac ha
                         inner join avail_cache.rate ra on (ra.hotel_id = ha.id)
                where ha.hotel_code in (select hotel_code
                                        from avail_cache.hotel_location
                                        where place_id = v_place_id.place_id)
                  and ha.avail_date = v_avail_date
                group by ra.currency
                having (min(ra.amount) is not null)
                   and min(ra.amount) > 0;
                insert into avail_cache.location_price
                values (v_place_id.place_id, v_avail_date, location_price_record.currency, location_price_record.price)
                on conflict on constraint location_price_pkey do update set currency=location_price_record.currency,
                                                                            price=location_price_record.price;
            end loop;
    end;
' LANGUAGE PLPGSQL;

--rollback DROP PROCEDURE IF EXISTS avail_cache.UPDATE_LOCATION_PRICE(DATE, VARCHAR);