delete from avail_cache.rate;
delete from avail_cache.room;
delete from avail_cache.hotel_ac;

insert into avail_cache.hotel_ac (id, avail_date, hotel_code, pms_source) values ('ch1', '2022-01-01', 'HOTELC', 'BART');
insert into avail_cache.room (id, quantity, type, hotel_id) values ('cr1',10,'DBL','ch1');

insert into avail_cache.hotel_ac (id, avail_date, hotel_code, pms_source) values ('ch2', '2022-01-01', 'BIRTOB', 'BART');
insert into avail_cache.room (id, quantity, type, hotel_id) values ('cr2',10,'DBL','ch2');
insert into avail_cache.room (id, quantity, type, hotel_id) values ('cr3',10,'SGL','ch2');

insert into avail_cache.hotel_ac (id, avail_date, hotel_code, pms_source) values ('ch3', '2022-01-01', 'KARCAW', 'OP');
insert into avail_cache.room (id, quantity, type, hotel_id) values ('cr4',10,'DBL','ch3');
insert into avail_cache.room (id, quantity, type, hotel_id) values ('cr5',5,'SGL','ch3');

insert into avail_cache.hotel_ac (id, avail_date, hotel_code, pms_source) values ('ch4', '2022-01-01', 'MATBAK', 'OP');
insert into avail_cache.room (id, quantity, type, hotel_id) values ('cr6',5,'SGL','ch4');
insert into avail_cache.rate (id, amount, avail, currency, min_nights, rate_category, max_nights,premium_amt, hotel_id,room_id)
values ('cr1', 101.00, true, 'G', 0, 'S',0,100.00,'ch4','cr6');
