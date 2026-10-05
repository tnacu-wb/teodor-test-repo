package uk.co.whitbread.ondemandrefreshservice.infrastructure.util;

import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.HotelEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.RatePlanEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.RoomEntity;

@Slf4j
public final class DBBatchUtil {

  private DBBatchUtil(){

  }

  public static final String BATCH_BEGIN = "do $$ <<available_cache>> " +
      "declare " +
      " v_record record; " +
      "begin ";
  public static final String BATCH_END = "end available_cache $$; ";

  public static final String INSERT_HOTEL = "select id from avail_cache.hotel_ac ha into v_record where id = '%s'; " +
      "if not found then " +
      "   insert into avail_cache.hotel_ac (id, avail_date, hotel_code, pms_source) values ('%s', '%s', '%s', '%s') " +
      "   on conflict on constraint hotel_ac_pkey do nothing; " +
      "end if; ";
  public static final String ROOM_VALUES = "insert into avail_cache.room (id, quantity, type, hotel_id) values ('%s',%s,'%s','%s') on conflict on constraint room_pkey do update set quantity=%s; ";

  public static final String RATE_VALUES = "insert into avail_cache.rate (id, amount, avail, currency, min_nights, rate_category, rate_code, max_nights, hotel_id, room_id) values ('%s', %s, %s, %s, %s, '%s','%s','%s', '%s', '%s') on conflict on constraint rate_pkey do update set amount=%s, avail=%s, min_nights=%s, currency=%s, max_nights=%s, room_id='%s'; ";

  public static final String UPDATE_HOTEL_TIME = "update avail_cache.hotel_ac set time_updated = CURRENT_TIMESTAMP where id = '%s'; ";

  public static String updateHotelTimeQuery(final String hotelId) {
    return String.format(UPDATE_HOTEL_TIME, hotelId);
  }

  public static final String insertConflictSafeHotel(final HotelEntity hotelEntity) {
    log.debug("insertConflictSafeHotel for hotelEntity - {}",hotelEntity);
    return String.format(INSERT_HOTEL, hotelEntity.getId(), hotelEntity.getId(), hotelEntity.getDate(), hotelEntity.getHotelCode(), hotelEntity.getPmsSource());
  }

  public static final String insertUpdateRoomQuery(final RoomEntity roomEntity,final String hotelId) {
    log.debug("insertUpdateRoomQuery for hotelId - {} and roomEntity - {}",hotelId,roomEntity);
    return String.format(ROOM_VALUES, roomEntity.getId(),
        roomEntity.getQuantity(), roomEntity.getRoomType(), hotelId, roomEntity.getQuantity());
  }

  public static final String insertUpdateRateQuery(final RatePlanEntity ratePlanEntity, final String hotelId, final String roomId) {
    log.debug("insertUpdateRateQuery for the hotelId - {} and rateEntity - {}",hotelId,ratePlanEntity);
    return String.format(RATE_VALUES,
        ratePlanEntity.getId(),
        ratePlanEntity.getAmount(), ratePlanEntity.isAvailability(),
        ratePlanEntity.getCurrency() == null ? null : "'" + ratePlanEntity.getCurrency() + "'",
        ratePlanEntity.getMinNights(), ratePlanEntity.getRateClassification(), ratePlanEntity.getRateCode(),
        ratePlanEntity.getMaxNights(), hotelId, roomId,
        ratePlanEntity.getAmount(),ratePlanEntity.isAvailability(),ratePlanEntity.getMinNights(),
        ratePlanEntity.getCurrency() == null ? null : "'" + ratePlanEntity.getCurrency() + "'",
        ratePlanEntity.getMaxNights(),
        roomId);
  }

}
