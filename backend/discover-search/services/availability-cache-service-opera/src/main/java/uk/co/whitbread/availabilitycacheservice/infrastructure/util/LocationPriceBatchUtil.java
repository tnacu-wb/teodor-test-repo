package uk.co.whitbread.availabilitycacheservice.infrastructure.util;

public final class LocationPriceBatchUtil {

//public static final String BATCH_BEGIN = "do $$ <<update_location_price>> " +
//   "declare " +
//   " location_price_record record; " +
//   "begin ";
//
//   public static final String FETCH_AND_INSERT_LOCATION_PRICE = "for location_price_record in\n" +
//   "     select ha.hotel_code, ha.avail_date, hl.place_id, min(ra.amount) price, ra.currency\n" +
//   "     from avail_cache.hotel_ac ha\n" +
//   "     inner join avail_cache.rate ra on (ra.hotel_id=ha.id)\n" +
//   "     inner join avail_cache.hotel_location hl on (hl.hotel_code=ha.hotel_code)\n" +
//   "     where ha.hotel_code = '%s' and ha.avail_date = '%s'\n" +
//   "     group by ha.hotel_code , ha.avail_date , ra.currency , hl.place_id \n" +
//   "     having (min(ra.amount) is not null) and min(ra.amount)>0\n" +
//   "     loop\n" +
//   "         insert into avail_cache.location_price values\n" +
//   "         (location_price_record.place_id, location_price_record.avail_date,
//   location_price_record.currency, location_price_record.price)\n" +
//   "         on conflict on constraint location_price_pkey do nothing;\n" +
//   "     end loop;";
//
//   public static final String BATCH_END = "end update_location_price $$; ";
//
//   public static final String insertConflictSafeLocationPrice(final HotelEntity entity) {
//   log.debug("insertConflictSafeRecord for LocationPriceEntity - {}",entity);
//   return String.format(FETCH_AND_INSERT_LOCATION_PRICE, entity.getHotelCode(), entity.getDate());
//   }
}
